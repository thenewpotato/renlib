package thenewpotato.renlib.homework;

import java.util.ArrayList;

public class Day {

    public ArrayList<Assignment> assignments;

    public Day(ArrayList<Assignment> assignments) {
        this.assignments = assignments;
    }

    public Day(){
        this.assignments = new ArrayList<>();
    }

}
