public class Student {
    public int id;
    public String name;
    public float avgMark;
    public String groupName;
    public int  course ;

    public Student(String name, float avgMark, String groupName, int course) {
        this.name = name;
        this.avgMark = avgMark;
        this.groupName = groupName;
        this.course = course;
    }
}
