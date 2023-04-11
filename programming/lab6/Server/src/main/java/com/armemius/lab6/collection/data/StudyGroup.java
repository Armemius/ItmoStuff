package com.armemius.lab6.collection.data;

import com.armemius.lab6.collection.exceptions.CollectionRuntimeException;

import java.io.Serial;
import java.io.Serializable;
import java.time.format.DateTimeFormatter;
import java.util.Objects;
import java.util.Set;
import java.util.TreeSet;

@SuppressWarnings("unused")
public class StudyGroup implements Serializable {
    public StudyGroup() {}

    private static final Set<Integer> usedIds = new TreeSet<>();
    public final static int MIN_ID = 0;
    public final static int MIN_STUDENTS_COUNT = 0;
    public final static int MIN_EXPELLED_STUDENTS_COUNT = 0;
    public final static double MIN_AVG_MARK = 0;

    public StudyGroup(Integer id,
                      String name,
                      Coordinates coordinates,
                      long studentsCount,
                      int expelledStudents,
                      double averageMark,
                      Semester semesterEnum,
                      Person groupAdmin) {
        creationDate = java.time.ZonedDateTime.now();
        setId(id);
        setName(name);
        setCoordinates(coordinates);
        setStudentsCount(studentsCount);
        setExpelledStudents(expelledStudents);
        setAverageMark(averageMark);
        setSemesterEnum(semesterEnum);
        setGroupAdmin(groupAdmin);
    }

    @Serial
    private static final long serialVersionUID = -903541336;
    private Integer id; //Поле не может быть null, Значение поля должно быть больше 0, Значение этого поля должно быть уникальным, Значение этого поля должно генерироваться автоматически
    private String name; //Поле не может быть null, Строка не может быть пустой
    private Coordinates coordinates; //Поле не может быть null
    private java.time.ZonedDateTime creationDate; //Поле не может быть null, Значение этого поля должно генерироваться автоматически
    private long studentsCount; //Значение поля должно быть больше 0
    private int expelledStudents; //Значение поля должно быть больше 0
    private double averageMark; //Значение поля должно быть больше 0
    private Semester semesterEnum; //Поле может быть null
    private Person groupAdmin; //Поле не может быть null

    public static Set<Integer> getUsedIds() {
        return usedIds;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        if (id == null || id <= MIN_ID || usedIds.contains(id)) {
            throw new CollectionRuntimeException("Incorrect id parameter for StudyGroup");
        }
        usedIds.add(id);
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        if (name == null || name.isEmpty()) {
            throw new CollectionRuntimeException("Incorrect name parameter for StudyGroup");
        }
        this.name = name;
    }

    public Coordinates getCoordinates() {
        return coordinates;
    }

    public void setCoordinates(Coordinates coordinates) {
        if (coordinates == null) {
            throw new CollectionRuntimeException("Incorrect coordinates parameter for StudyGroup");
        }
        this.coordinates = coordinates;
    }

    public java.time.ZonedDateTime getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(java.time.ZonedDateTime creationDate) {
        this.creationDate = creationDate;
    }

    public long getStudentsCount() {
        return studentsCount;
    }

    public void setStudentsCount(long studentsCount) {
        if (studentsCount <= MIN_STUDENTS_COUNT) {
            throw new CollectionRuntimeException("Incorrect studentsCount parameter for StudyGroup");
        }
        this.studentsCount = studentsCount;
    }

    public int getExpelledStudents() {
        return expelledStudents;
    }

    public void setExpelledStudents(int expelledStudents) {
        if (expelledStudents <= MIN_EXPELLED_STUDENTS_COUNT) {
            throw new CollectionRuntimeException("Incorrect expelledStudents parameter for StudyGroup");
        }
        this.expelledStudents = expelledStudents;
    }

    public double getAverageMark() {
        return averageMark;
    }

    public void setAverageMark(double averageMark) {
        if (averageMark <= MIN_AVG_MARK) {
            throw new CollectionRuntimeException("Incorrect avgMark parameter for StudyGroup");
        }
        this.averageMark = averageMark;
    }

    public Semester getSemesterEnum() {
        return semesterEnum;
    }

    public void setSemesterEnum(Semester semesterEnum) {
        this.semesterEnum = semesterEnum;
    }

    public Person getGroupAdmin() {
        return groupAdmin;
    }

    public void setGroupAdmin(Person groupAdmin) {
        if (groupAdmin == null) {
            throw new CollectionRuntimeException("Incorrect groupAdmin parameter for StudyGroup");
        }
        this.groupAdmin = groupAdmin;
    }

    @Override
    public String toString() {
        return "StudyGroup(" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", coordinates=" + coordinates +
                ", creationDate=" + creationDate.format(DateTimeFormatter.ofPattern("HH:mm:ss dd/MM/yyyy")) +
                ", studentsCount=" + studentsCount +
                ", expelledStudents=" + expelledStudents +
                ", averageMark=" + averageMark +
                ", semesterEnum=" + semesterEnum +
                ", groupAdmin=" + groupAdmin +
                ')';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null) {
            return false;
        }
        if (!(o instanceof StudyGroup group)) {
            return false;
        }
        return studentsCount == group.studentsCount
                && expelledStudents == group.expelledStudents
                && Double.compare(group.averageMark, averageMark) == 0
                && id.equals(group.id) && name.equals(group.name)
                && coordinates.equals(group.coordinates)
                && creationDate.equals(group.creationDate)
                && semesterEnum == group.semesterEnum
                && groupAdmin.equals(group.groupAdmin);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id,
                name,
                coordinates,
                creationDate,
                studentsCount,
                expelledStudents,
                averageMark,
                semesterEnum,
                groupAdmin);
    }
}