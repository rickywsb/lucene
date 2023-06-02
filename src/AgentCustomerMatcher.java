import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.*;
import org.apache.lucene.index.*;
import org.apache.lucene.search.*;
import org.apache.lucene.store.ByteBuffersDirectory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class AgentCustomerMatcher {
  private ByteBuffersDirectory index;
  private StandardAnalyzer analyzer;

  public AgentCustomerMatcher() {
    this.index = new ByteBuffersDirectory();
    this.analyzer = new StandardAnalyzer();
  }

  public void indexCustomer(Customer customer) throws IOException {
    IndexWriterConfig config = new IndexWriterConfig(analyzer);
    IndexWriter writer = new IndexWriter(index, config);

    for (Attribute attribute : customer.getAttributes()) {
      Document doc = new Document();
      doc.add(new StringField("id", customer.getId(), Field.Store.YES));
      doc.add(new StringField("attributeName", attribute.getName(), Field.Store.YES));
      doc.add(new IntPoint("proficiency", attribute.getProficiency()));
      doc.add(new StoredField("proficiency", attribute.getProficiency()));
      doc.add(new LongPoint("enqueueTime", customer.getEnqueueTime()));
      doc.add(new NumericDocValuesField("enqueueTime", customer.getEnqueueTime()));
      writer.addDocument(doc);
    }

    writer.close();
  }

  public List<Customer> findMatchingCustomers(Agent agent) throws IOException {
    List<Customer> matchingCustomers = new ArrayList<>();

    IndexReader reader = DirectoryReader.open(index);
    IndexSearcher searcher = new IndexSearcher(reader);

    BooleanQuery.Builder finalQuery = new BooleanQuery.Builder();

    for (Attribute attribute : agent.getAttributes()) {
      Query query = new BooleanQuery.Builder()
              .add(new TermQuery(new Term("attributeName", attribute.getName())), BooleanClause.Occur.MUST)
              .add(IntPoint.newRangeQuery("proficiency", attribute.getProficiency(), 5), BooleanClause.Occur.MUST)
              .build();
      finalQuery.add(query, BooleanClause.Occur.SHOULD);
    }
    TopDocs topDocs = searcher.search(finalQuery.build(), 10, new Sort(new SortField("enqueueTime", SortField.Type.LONG, true)));

    for (ScoreDoc scoreDoc : topDocs.scoreDocs) {
      Document doc = searcher.doc(scoreDoc.doc);
      Customer customer = new Customer();
      customer.setId(doc.get("id"));
      customer.setEnqueueTime(Long.parseLong(doc.get("enqueueTime")));

      matchingCustomers.add(customer);
    }

    reader.close();
    return matchingCustomers;
  }
}