import com.murcia.utils.ListaEnlazada;
import com.murcia.utils.ColaEnlazada;
import java.util.*;
import java.io.*;
 
class Main {
    public static void main(String[] args) throws Exception {
        File file = new File("randomtext.txt");
        // Si no existe el archivo, lo crea vacío
        if (!file.exists()) file.createNewFile();
 
        Scanner sc = new Scanner(file);
        ListaEnlazada<StringBuilder> l = new ColaEnlazada<>();
 
        while (sc.hasNextLine()) {
            String line = sc.nextLine();
            String[] words = line.split("\\s+");
            for (String i : words)
                if (!i.isEmpty())
                    l.addLast(new StringBuilder(i));
            l.addLast(new StringBuilder("\n"));
        }
 
        // Si el archivo estaba vacío, agregar al menos un nodo vacío
        if (l.size() == 0) {
            l.addLast(new StringBuilder(""));
            l.addLast(new StringBuilder("\n"));
        }
 
        Editor edit = new Editor(l);
        sc = new Scanner(System.in);
 
        while (true) {
            System.out.println("S - Save and Exit");
            System.out.println("F - Move Forward");
            System.out.println("B - Move Backward");
            System.out.println("b - Move to Beginning of Line");
            System.out.println("e - Move to End of Line");
            System.out.println("i - Insert");
            System.out.println("d - Delete");
            edit.print();
            edit.printCursor();
 
            String input = sc.nextLine();
            if (input.equals("S")) break;
            else if (input.equals("F")) edit.forward();
            else if (input.equals("B")) edit.backward();
            else if (input.equals("b")) edit.beginning();
            else if (input.equals("e")) edit.ending();
            else if (input.equals("i")) edit.insert(sc.nextLine());
            else if (input.equals("d")) edit.delete();
 
            System.out.print("\033[H\033[2J");
            System.out.flush();
            file.delete();
            file.createNewFile();
        }
 
        edit.writer.close();
    }
}
 
class Editor {
    ListaEnlazada<StringBuilder> l;
    int node, position;
    FileWriter writer;
 
    public Editor(ListaEnlazada<StringBuilder> l) {
        this.l = l;
        node = position = 0;
    }
 
    public void forward() {
        StringBuilder s = (StringBuilder) l.get(node);
        if (position == s.length()) {
            if (node == l.size() - 1)
                node = 0;
            else
                node++;
            position = 0;
        } else {
            position++;
        }
        s = (StringBuilder) l.get(node);
        if (s.toString().equals("\n")) forward();
    }
 
    public void backward() {
        StringBuilder s = (StringBuilder) l.get(node);
        if (position == 0) {
            if (node == 0)
                node = l.size() - 1;
            else
                node--;
            s = (StringBuilder) l.get(node);
            position = s.length();
        } else {
            position--;
        }
        s = (StringBuilder) l.get(node);
        if (s.toString().equals("\n")) backward();
    }
 
    public void insert(String input) {
        StringBuilder s = (StringBuilder) l.get(node);
        if (s.length() == 0)
            s.append(input);
        else
            s.insert(position, input);
        position += input.length();
    }
 
    public void delete() {
        StringBuilder s = (StringBuilder) l.get(node);
        if (position == 0 && node > 0) {
            StringBuilder prev = (StringBuilder) l.get(node - 1);
            position = prev.length();
            prev.append(s);
            l.remove(node);
        } else if (position > 0) {
            s.delete(position - 1, position);
            position--;
        }
    }
 
    public void beginning() {
        while (node > 0 && !((StringBuilder) l.get(node)).toString().equals("\n"))
            node--;
        if (node != 0)
            node = node + 1;
        position = 0;
    }
 
    public void ending() {
        while (!((StringBuilder) l.get(node)).toString().equals("\n"))
            node++;
        node = node - 1;
        position = ((StringBuilder) l.get(node)).length();
    }
 
    public void print() throws Exception {
        writer = new FileWriter("randomtext.txt");
        for (int i = 0; i < l.size(); i++) {
            StringBuilder sb = (StringBuilder) l.get(i);
            writer.write(sb.toString());
            System.out.print(sb);
            if (!sb.toString().equals("\n")) {
                writer.write(" ");
                System.out.print(" ");
            }
        }
    }
 
    public void printCursor() {
        if (l.size() == 0) return;
        System.out.print("Cursor At : ");
        StringBuilder s = (StringBuilder) l.get(node);
        for (int i = 0; i < s.length(); i++) {
            if (i == position) System.out.print("|");
            System.out.print(s.charAt(i));
        }
        if ((position == 0 && s.length() == 0) || (position == s.length()))
            System.out.print("|");
        System.out.println();
    }
}