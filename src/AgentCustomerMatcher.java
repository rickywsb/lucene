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

  public void indexAgent(Agent agent) throws IOException {
    IndexWriterConfig config = new IndexWriterConfig(analyzer);
    IndexWriter writer = new IndexWriter(index, config);

    for (Attribute attribute : agent.getAttributes()) {
      Document doc = new Document();
      doc.add(new StringField("id", agent.getId(), Field.Store.YES));
      doc.add(new StringField("attributeName", attribute.getName(), Field.Store.YES));
      doc.add(new IntPoint("proficiency", attribute.getProficiency()));
      doc.add(new StoredField("proficiency", attribute.getProficiency()));
      writer.addDocument(doc);
    }

    writer.close();
  }

  public List<Agent> findMatchingAgents(Customer customer) throws IOException {
    List<Agent> matchingAgents = new ArrayList<>();

    IndexReader reader = DirectoryReader.open(index);
    IndexSearcher searcher = new IndexSearcher(reader);

    for (Attribute attribute : customer.getAttributes()) {
      Query query = new BooleanQuery.Builder()
              .add(new TermQuery(new Term("attributeName", attribute.getName())), BooleanClause.Occur.MUST)
              .add(IntPoint.newRangeQuery("proficiency", attribute.getProficiency(), 5), BooleanClause.Occur.MUST)
              .build();

      TopDocs topDocs = searcher.search(query, 10);
      for (ScoreDoc scoreDoc : topDocs.scoreDocs) {
        Document doc = searcher.doc(scoreDoc.doc);
        Agent agent = new Agent(doc.get("id"), null); // we're not fetching agent attributes here
        matchingAgents.add(agent);
      }
    }

    reader.close();
    return matchingAgents;
  }
}
