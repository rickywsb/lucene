import lombok.Data;

@Data
public class Attribute {
  private String name;
  private int proficiencyLevel;

  public Attribute(String name, int proficiencyLevel) {
    this.name = name;
    this.proficiencyLevel = proficiencyLevel;
  }

  public int getProficiencyLevel(){
    return proficiencyLevel;
  }

  public int getProficiency(){
    return proficiencyLevel;
  }

  public String getName(){
    return name;
  }


}