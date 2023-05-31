import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.IntPoint;
import org.apache.lucene.document.StoredField;
import org.apache.lucene.document.StringField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.ByteBuffersDirectory;
import org.apache.lucene.store.Directory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class AgentCustomerMatcher {
  private IndexWriter writer;
  private StandardAnalyzer analyzer;

  public AgentCustomerMatcher() throws IOException {
    Directory memoryIndex = new ByteBuffersDirectory();
    analyzer = new StandardAnalyzer();
    IndexWriterConfig config = new IndexWriterConfig(analyzer);
    writer = new IndexWriter(memoryIndex, config);
  }

  public void indexAgent(Agent agent) throws IOException {
    Document doc = new Document();
    doc.add(new StringField("id", agent.getId(), Field.Store.YES));
    doc.add(new StringField("skill", agent.getSkill(), Field.Store.YES));
    doc.add(new IntPoint("proficiencyLevel", agent.getProficiencyLevel()));
    doc.add(new StoredField("proficiencyLevel", agent.getProficiencyLevel()));
    writer.addDocument(doc);
    writer.commit();
  }

  public List<Agent> findMatchingAgents(Customer customer) throws IOException {
    List<Agent> matchingAgents = new ArrayList<>();
    IndexReader reader = DirectoryReader.open(writer.getDirectory());
    IndexSearcher searcher = new IndexSearcher(reader);

    Query skillQuery = new TermQuery(new Term("skill", customer.getSkill()));
    Query proficiencyQuery = IntPoint.newRangeQuery("proficiencyLevel", customer.getProficiencyLevel(), 5);
    BooleanQuery booleanQuery = new BooleanQuery.Builder()
            .add(skillQuery, BooleanClause.Occur.MUST)
            .add(proficiencyQuery, BooleanClause.Occur.MUST)
            .build();

    TopDocs topDocs = searcher.search(booleanQuery, 10);
    for (ScoreDoc scoreDoc : topDocs.scoreDocs) {
      Document doc = searcher.doc(scoreDoc.doc);
      Agent agent = new Agent();
      agent.setId(doc.get("id"));
      agent.setSkill(doc.get("skill"));
      agent.setProficiencyLevel(Integer.parseInt(doc.get("proficiencyLevel")));
      matchingAgents.add(agent);
    }
    reader.close();

    return matchingAgents;
  }
}
