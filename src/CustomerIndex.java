import org.apache.lucene.document.*;
import org.apache.lucene.index.IndexWriter;

import java.io.IOException;
import java.util.List;

public class CustomerIndex {
  private IndexWriter writer;

  public CustomerIndex(IndexWriter writer) {
    this.writer = writer;
  }

  public void indexCustomer(Customer customer) throws IOException {
    Document doc = new Document();
    doc.add(new StringField("id", customer.getId(), Field.Store.YES));
    doc.add(new LongPoint("enqueueTime", customer.getEnqueueTime()));
    doc.add(new StoredField("enqueueTime", customer.getEnqueueTime()));

    List<Attribute> attributes = customer.getAttributes();
    for (Attribute attribute : attributes) {
      doc.add(new StringField("attribute", attribute.getName(), Field.Store.YES));
      doc.add(new IntPoint("proficiency", attribute.getProficiency()));
      doc.add(new StoredField("proficiency", attribute.getProficiency()));
    }

    writer.addDocument(doc);
    writer.commit();
  }
}