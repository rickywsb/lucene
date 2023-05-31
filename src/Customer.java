import lombok.Data;


@Data
public class Customer {
  private String id;
  private String skill;
  private int proficiencyLevel;

  void setId(String id) {
    this.id = id;
  }
  void setSkill(String skill) {
    this.skill = skill;
  }
  void setProficiencyLevel(int proficiencyLevel) {
    this.proficiencyLevel = proficiencyLevel;
  }
  public String getId(){
    return id;
  }
  public String getSkill() {
    return skill;
  }

  public int getProficiencyLevel(){
    return proficiencyLevel;
  }

}