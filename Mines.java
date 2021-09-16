/* ΟΝΟΜΑΤΕΠΩΝΥΜΟ: ΜΑΤΣΚΙΔΗΣ ΑΘΑΝΑΣΙΟΣ
*  ΕΤΟΙΜΟΣ ΚΩΔΙΚΑΣ: ΤΟ ΔΙΑΒΑΣΜΑ ΤΟΥ ΑΡΧΕΙΟΥ ΩΣ ΟΡΙΣΜΑ ΤΟ ΟΠΟΙΟ ΠΗΡΑ ΑΠΟ https://www2.hawaii.edu/~walbritt/ics211/examples/ReadFromFile.java
*/

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Scanner;

public class Mines {

    /* ΑΝΑΠΑΡΑΣΤΑΣΗ ΤΟΥ ΖΕΥΓΟΥΣ ΣΗΜΕΙΩΝ (X, Y) ΜΕ ΤΟ ΑΝΤΙΚΕΙΜΕΝΟ Position. */
    static class Position {

        private int xPosition, yPosition;

        public Position(int xPosition, int yPosition) {
            this.xPosition = xPosition;
            this.yPosition = yPosition;
        }

        public int getxPosition() {
            return xPosition;
        }

        public void setxPosition(int xPosition) {
            this.xPosition = xPosition;
        }

        public int getyPosition() {
            return yPosition;
        }

        public void setyPosition(int yPosition) {
            this.yPosition = yPosition;
        }

    }

    private static ArrayList<Position> convexHull = new ArrayList<Position>(); //ΣΥΝΟΛΟ ΠΟΥ ΠΕΡΙΕΧΕΙ ΤΑ ΣΤΟΙΧΕΙΑ ΤΟΥ ΚΥΡΤΟΥ ΠΕΡΙΒΛΗΜΑΤΟΣ

    /* quickHull: ΔΕΧΕΤΑΙ ΩΣ ΟΡΙΣΜΑ ΤΟ ΣΥΝΟΛΟ ΤΩΝ ΣΗΜΕΙΩΝ ΤΑΞΙΝΟΜΗΜΕΝΑ ΚΑΙ ΕΠΙΣΤΡΕΦΕΙ ΤΟ ΣΥΝΟΛΟ ΤΩΝ ΣΗΜΕΙΩΝ ΤΟΥ ΣΥΝΤΟΜΟΤΕΡΟΥ ΜΟΝΟΠΑΤΙΟΥ.
    *  ΤΟΠΟΘΕΤΕΙ ΤΑ ΣΗΜΕΙΑ ΤΗΣ ΑΡΧΗΣ ΚΑΙ ΤΟΥ ΤΕΛΟΥΣ ΤΗΣ ΔΙΑΔΡΟΜΗΣ ΣΤΟ convexHull ΚΑΙ ΔΗΜΙΟΥΡΓΕΙ 2 ΣΥΝΟΛΑ (S1, S2) ΣΤΑ ΟΠΟΙΑ ΤΟΠΟΘΕΤΕΙ
    *  ΣΗΜΕΙΑ ΒΑΣΗ ΤΗΣ ΘΕΣΗΣ ΤΟΥΣ ΜΕ ΤΗΝ ΕΥΘΕΙΑ ΠΟΥ ΔΗΜΙΟΥΡΓΕΙΤΑΙ ΑΠΟ ΤΟ ΣΗΜΕΙΟ ΤΗΣ ΑΡΧΗΣ ΚΑΙ ΤΟΥ ΤΕΛΟΥΣ.
    */
    public ArrayList<Position> quickHull(ArrayList<Position> positionList){

        Position start = positionList.get(0);
        convexHull.add(start);
        positionList.remove(start);

        Position end = positionList.get(positionList.size() - 1);
        convexHull.add(end);
        positionList.remove(end);

        ArrayList<Position> S1 = new ArrayList<Position>();
        ArrayList<Position> S2 = new ArrayList<Position>();

        for (int i = 0; i < positionList.size(); i++){
            if (S1_Or_S2(start, end, positionList.get(i)) == 1){
                S1.add(positionList.get(i));
            }
            else if (S1_Or_S2(start, end, positionList.get(i)) == -1){
                S2.add(positionList.get(i));
            }
        }

        hullSet(start, end, S1);
        hullSet(end, start, S2);

        return getThePath();
    }

    /* area: ΥΠΟΛΟΓΙΖΕΙ ΤΟ ΕΜΒΑΔΟ ΤΟΥ ΤΡΙΓΏΝΟΥ ΠΟΥ ΔΗΜΙΟΥΡΓΕΙΤΑΙ ΑΠΟ ΤΑ ΤΡΙΑ ΣΗΜΕΙΑ ΠΟΥ ΔΕΧΕΤΑΙ ΣΑΝ ΟΡΙΣΜΑ ΒΑΣΗ ΤΗΣ ΟΡΙΖΟΥΣΑΣ. */
    public double area(Position a, Position b, Position c){
        return (a.xPosition * b.yPosition + c.xPosition * a.yPosition + b.xPosition * c.yPosition - c.xPosition * b.yPosition - b.xPosition * a.yPosition - a.xPosition * c.yPosition)/2.0;
    }

    /* S1_Or_S2: ΑΝΑΛΟΓΑ ΜΕ ΤΗΝ ΘΕΣΗ ΤΟΥ ΣΗΜΕΙΟΥ (c) ΜΕ ΤΗΝ ΕΥΘΕΙΑ ΠΟΥ ΔΗΜΙΟΥΡΓΕΙΤΑΙ ΑΠΟ ΤΟ ΣΗΜΕΙΟ ΤΗΣ ΑΡΧΗΣ ΚΑΙ ΤΟΥ ΤΕΛΟΥΣ
     * ΕΠΙΣΤΡΕΦΕΤΑΙ Ο ΚΑΤΑΛΛΗΛΟΣ ΑΡΙΘΜΟΣ ΠΟΥ ΘΑ ΚΡΙΝΕΙ ΑΝ Ο ΑΡΙΘΜΟΣ (c) ΘΑ "ΜΠΕΙ" ΣΤΟ ΣΥΝΟΛΟ S1 Ή ΣΤΟ ΣΥΝΟΛΟ S2. */
    public int S1_Or_S2(Position a, Position b, Position c){

        double locate = area(a, b, c);

        if (locate > 0){
            return 1;
        }
        else if (locate < 0){
            return -1;
        }
        else{
            return 0;
        }
    }

    /* hullSet: ΣΥΝΑΡΤΗΣΗ ΠΟΥ ΥΠΟΛΟΓΙΖΕΙ ΑΝΑΔΡΟΜΙΚΑ ΤΟ ΚΥΡΤΟ ΠΕΡΙΒΛΗΜΑ (convexHull).*/
    public void hullSet(Position a, Position b, ArrayList<Position> S){

        int indexOfPMax = 0;

        if (S.size() == 0){ //ΑΝ ΤΟ ΣΥΝΟΛΟ ΕΙΝΑΙ ΑΔΕΙΟ ΤΟΤΕ ΤΕΡΜΑΤΙΖΕΙ ΧΩΡΙΣ ΝΑ ΚΑΝΕΙ ΚΑΠΟΙΑ ΕΝΕΡΓΕΙΑ.
            return;
        }

        if (S.size() == 1){ //ΑΝ ΤΟ ΣΗΝΟΛΟ ΑΠΟΤΕΛΕΙΤΑΙ ΑΠΟ ΕΝΑ ΣΗΜΕΙΟ ΤΟΤΕ ΑΥΤΟΜΑΤΩΣ "ΜΠΑΙΝΕΙ" ΣΤΟ ΚΥΡΤΟ ΠΕΡΙΒΛΗΜΑ ΚΑΙ ΤΕΡΜΑΤΙΖΕΙ.
            Position p = S.get(0);
            convexHull.add(p);
            return;
        }

        double maxTriangleArea = 0;

        for (int i = 0; i < S.size(); i++){
            Position c = S.get(i);
            if (area(a, b, c) > maxTriangleArea){
                maxTriangleArea = area(a, b, c);
                indexOfPMax = i;
            }
        }

        Position P = S.get(indexOfPMax);
        S.remove(P);
        convexHull.add(P);

        ArrayList<Position> leftSetAP = new ArrayList<Position>();
        for (int i = 0; i < S.size(); i++){
            Position M = S.get(i);
            if (S1_Or_S2(a, P, M) == 1){
                leftSetAP.add(M);
            }
        }

        ArrayList<Position> leftSetPB = new ArrayList<Position>();
        for (int i = 0; i < S.size(); i++){
            Position M = S.get(i);
            if (S1_Or_S2(P, b, M) == 1){
                leftSetPB.add(M);
            }
        }

        hullSet(a, P, leftSetAP); //ΑΝΑΔΡΟΜΙΚΗ ΚΛΗΣΗ
        hullSet(P, b, leftSetPB); //ΑΝΑΔΡΟΜΙΚΗ ΚΛΗΣΗ
    }

    /* getThePath: ΣΥΝΑΡΤΗΣΗ ΠΟΥ ΥΠΟΛΟΓΙΖΕΙ ΤΑ ΚΥΡΤΑ ΠΕΡΙΒΛΗΜΑΤΑ ΤΩΝ ΣΥΝΟΛΩΝ S1 ΚΑΙ S2 ΚΑΙ ΕΠΙΣΤΡΕΦΕΙ ΤΟ ΚΥΡΤΟ ΠΕΡΙΒΛΗΜΑ ΜΕ ΤΗ ΜΙΚΡΟΤΕΡΗ ΔΙΑΔΡΟΜΗ. */
    public ArrayList<Position> getThePath(){

        ArrayList<Position> convexHullS1 = new ArrayList<Position>();
        ArrayList<Position> convexHullS2 = new ArrayList<Position>();

        convexHullS1.add(convexHull.get(0));
        convexHullS1.add(convexHull.get(1));
        convexHullS2.add(convexHull.get(0));
        convexHullS2.add(convexHull.get(1));

        int y = convexHull.get(0).yPosition;
        if (convexHull.get(1).yPosition < y){
            y = convexHull.get(1).yPosition;
        }

        for (int i = 2; i < convexHull.size(); i++){
            if (convexHull.get(i).yPosition < y){
                convexHullS2.add(convexHull.get(i));
            }
            else{
                convexHullS1.add(convexHull.get(i));
            }
        }

        /* ΤΑΞΙΝΟΜΗΣΗ ΤΩΝ ΣΗΜΕΙΩΝ ΤΟΥ ΚΥΡΤΟΥ ΠΕΡΙΒΛΗΜΑΤΟΣ ΤΟΥ S1 (ΒΑΣΗ ΤΟΥ Χ). */
        convexHullS1.sort(new Comparator<Position>() {
            @Override
            public int compare(Position o1, Position o2) {
                return o1.xPosition - o2.xPosition;
            }
        });

        /* ΤΑΞΙΝΟΜΗΣΗ ΤΩΝ ΣΗΜΕΙΩΝ ΤΟΥ ΚΥΡΤΟΥ ΠΕΡΙΒΛΗΜΑΤΟΣ ΤΟΥ S2 (ΒΑΣΗ ΤΟΥ Χ). */
        convexHullS2.sort(new Comparator<Position>() {
            @Override
            public int compare(Position o1, Position o2) {
                return o1.xPosition - o2.xPosition;
            }
        });

        double sumS1 = 0;
        for (int i = 0; i < convexHullS1.size() - 1 ; i++){
            sumS1 += twoPointsDistance(convexHullS1.get(i), convexHullS1.get(i+1));
        }

        double sumS2 = 0;
        for (int i = 0; i < convexHullS2.size() - 1 ; i++){
            sumS2 += twoPointsDistance(convexHullS2.get(i), convexHullS2.get(i+1));
        }

        if (sumS1 < sumS2){
            return convexHullS1;
        }
        else{
            return convexHullS2;
        }
    }

    /* twoPointsDistance: ΣΥΝΑΡΤΗΣΗ ΠΟΥ ΥΠΟΛΟΓΙΖΕΙ ΤΗΝ ΑΠΟΣΤΑΣΗ ΜΕΤΑΞΥ ΔΥΟ ΣΗΜΕΙΩΝ ΠΟΥ ΔΕΧΕΤΑΙ ΩΣ ΟΡΙΣΜΑΤΑ. */
    public static double twoPointsDistance(Position a, Position b){

        return Math.sqrt(Math.pow((b.xPosition - a.xPosition), 2) + Math.pow((b.yPosition - a.yPosition), 2));
    }

    /* print: ΣΥΝΑΡΤΗΣΗ Η ΟΠΟΙΑ ΕΜΦΑΝΙΖΕΙ ΤΑ ΑΠΟΤΕΛΕΣΜΑΤΑ. */
    public void print(double distance, ArrayList<Position> finalHull){
        System.out.println("The shortest distance is " + Math.floor(distance * 100000 + 0.5) / 100000);
        System.out.print("The shortest path is:");
        for (int i = 0; i < finalHull.size(); i++){
            if (i != (finalHull.size() - 1)){
                System.out.print("(" + finalHull.get(i).xPosition + "," + finalHull.get(i).yPosition + ")-->");
            }
            else{
                System.out.print("(" + finalHull.get(i).xPosition + "," + finalHull.get(i).yPosition + ")");
            }
        }
    }

    public static void main(String[] args) {
        File file = null;
        Scanner readFromFile = null;
        String line = null;

        /* ΠΕΡΙΠΤΩΣΗ ΟΠΟΥ ΔΕΝ ΥΠΑΡΧΕΙ ΚΑΝΕΝΑ ΑΡΧΕΙΟ ΩΣ ΟΡΙΣΜΑ. */
        if (args.length == 0){
            System.out.println("ERROR: Please enter the file name as the first commandline argument.");
            System.exit(1);
        }

        /* ΠΕΡΙΠΤΩΣΗ ΑΔΥΝΑΜΙΑΣ ΕΥΡΕΣΗΣ ΤΟΥ ΑΡΧΕΙΟΥ. */
        file = new File(args[0]);
        try{
            readFromFile = new Scanner(file);
        }catch (FileNotFoundException exception){
            System.out.println("ERROR: File not found for \"");
            System.out.println(args[0]+"\"");
            System.exit(1);
        }

        /* ΔΗΜΙΟΥΡΓΙΑ ΣΥΝΟΛΟΥ ΟΛΩΝ ΤΩΝ ΣΗΜΕΙΩΝ. */
        ArrayList<Position> positionsList = new ArrayList<>();
        while (readFromFile.hasNextLine()){
            line=readFromFile.nextLine();
            if (line.split(" ").length > 1) {
                Position position = new Position(Integer.parseInt(line.split(" ")[0]), Integer.parseInt(line.split(" ")[1]));
                positionsList.add(position);
            }
        }

        /* ΤΑΞΙΝΟΜΗΣΗ ΤΟΥ ΣΥΝΟΜΟΥ ΤΩΝ ΣΗΜΕΙΩΝ (ΒΑΣΗ ΤΟΥ Χ). */
        positionsList.sort(new Comparator<Position>() {
            @Override
            public int compare(Position o1, Position o2) {
                return o1.xPosition - o2.xPosition;
            }
        });

        Mines QuickHull = new Mines();
        ArrayList<Position> hull = QuickHull.quickHull(positionsList);
        double sum = 0;
        for (int i = 0; i < hull.size() - 1; i++){
            sum += twoPointsDistance(hull.get(i), hull.get(i+1));
        }
        QuickHull.print(sum, hull);
    }
}
