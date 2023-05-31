import org.apache.lucene.document.*;
import org.apache.lucene.index.IndexWriter;

import java.io.IOException;
import java.util.List;

public class AgentIndex {
  private IndexWriter writer;

  public AgentIndex(IndexWriter writer) {
    this.writer = writer;
  }

  public void indexAgent(Agent agent) throws IOException {
    Document doc = new Document();
    doc.add(new StringField("id", agent.getId(), Field.Store.YES));

    List<Attribute> attributes = agent.getAttributes();
    for (Attribute attribute : attributes) {
      doc.add(new StringField("attribute", attribute.getName(), Field.Store.YES));
      doc.add(new IntPoint("proficiencyLevel", attribute.getProficiencyLevel()));
      doc.add(new StoredField("proficiencyLevel", attribute.getProficiencyLevel()));
    }

    writer.addDocument(doc);
    writer.commit();
  }
}