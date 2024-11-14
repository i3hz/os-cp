import java.io.*;
import java.util.*;
// create a process control block to keep track of all the variables and functions of the partcular process
class PCB {
    public char jobID[] = new char[4];
    public char TTL[] = new char[4];
    public char TLL[] = new char[4];

    PCB() {
        Arrays.fill(jobID, '0');
        Arrays.fill(TTL, '0');
        Arrays.fill(TLL, '0');
    }

    public PCB(char jobID[], char TTL[], char TLL[]) {
        this.jobID = jobID;
        this.TTL = TTL;
        this.TLL = TLL;
    }

   

    public String getJobID() {
        String jobstr = new String(jobID);
        return jobstr;
    }

    public void setJobID(char[] jobID) {
        this.jobID = jobID;
    }

    public char[] getTTL() {
        return TTL;
    }

    public void setTTL(char[] TTL) {
        this.TTL = TTL;
    }

    public char[] getTLL() {
        return TLL;
    }

    public void setTLL(char[] TLL) {
        this.TLL = TLL;
    }

}

//creating a 2d array for main memory
class MainMemory {
    private char[][] M = new char[300][4];

    public MainMemory() {
        this.M = new char[300][4];
    }

    public char[][] getMemory() {
        return this.M;
    }

    public void setMemory(char[][] M) {
        this.M = M;
    }
}

class OperatingSystem {

    boolean isExceeded = false;
    boolean reachedH = false;

    private FileReader input;
    private FileWriter output;

    private BufferedReader inputReader;
    private BufferedWriter outputReader;

    private char[] buffer = new char[40];
    private int used_memory = 0;
    private int PTR;
    public int random[] = new int[30];

    
    private PCB pcb;
    
    private int TTC = 0;
    private int LLC = 0;
    private int ttl;
    private int tll;

    private int valid = 0;

    int realAddress = 0;

    boolean flag = true;
    char[][] M = new char[300][4];

    private char[] IR = new char[4]; 
    private boolean C = false; 
    private int IC = 0; 
    private char[] R = new char[4]; 

   
    private int SI = 0;
    private int PI = 0;
    private int TI = 0;

    public char[] getIR() {
        return this.IR;
    }

    public int getIR(int index) {
        return this.IR[index];
    }

    public void setIR(char[] IR) {
        this.IR = IR;
    }

    public boolean getC() {
        return this.C;
    }

    public void setC(boolean C) {
        this.C = C;
    }

    public int getIC() {
        return this.IC;
    }

    public void setIC(int IC) {
        this.IC = IC;
    }

    public char[] getR() {
        return this.R;
    }

    public void setR(char[] R) {
        this.R = R;
    }

    public int getPI() {
        return PI;
    }

    public void setPI(int PI) {
        this.PI = PI;
    }

    public int getTI() {
        return TI;
    }

    public void setTI(int TI) {
        this.TI = TI;
    }

    public int getSI() {
        return SI;
    }

    public void setSI(int sI) {
        SI = sI;
    }

  
    public String getOpcode() {
        String opcode = "";
        opcode += this.IR[0];
        if (opcode.equals("H")) {
            return opcode;
        }
        opcode += this.IR[1];
        return opcode;
    }

    public void printMemory() {
        for (int i = 0; i < M.length; i++) {
            System.out.print(i + " | ");

            for (int j = 0; j < M[0].length; j++) {
                System.out.print(M[i][j] + " | ");
            }
            System.out.println("");

        }
    }

    public int getOperand() {
        return Integer.parseInt(String.valueOf(this.IR[2]) + String.valueOf(this.IR[3]));
    }

    public OperatingSystem(String input, String output) {
        try {
            this.input = new FileReader(input);
            this.output = new FileWriter(output);
            this.inputReader = new BufferedReader(this.input);
            this.outputReader = new BufferedWriter(this.output);
        } catch (Exception e) {
            System.out.println("Error in initializing the input and output file");
        }
    }

    
    public void init() {
        Arrays.fill(IR, '0'); 
        this.C = false; 
        IC = 0; 
        Arrays.fill(R, '0'); 
        Arrays.fill(this.random, 0);
       
        used_memory = 0;

       
        this.PTR = 0;
        M = new char[300][4];

     
        this.pcb = new PCB();
    }

    public Map.Entry<Integer, int[]> allocate(int[] arr) {

       
        int value = (int) (Math.random() * 30);

        
        while (true) {
            if (arr[value] == 0) {
                arr[value] = 1;
                break;
            } else {
                value = (int) (Math.random() * 30);
            }
        }
        Map.Entry<Integer, int[]> pair = new AbstractMap.SimpleEntry<Integer, int[]>(value, arr);

        return pair;
    }

    public void LOAD() {
        String line = "";

        try {
            while ((line = inputReader.readLine()) != null) {
                buffer = line.toCharArray(); 

                if (buffer[0] == '$' && buffer[1] == 'A' && buffer[2] == 'M' && buffer[3] == 'J') {
                    System.out.println(
                            "Program card detected with JobID : " + buffer[4] + buffer[5] + buffer[6] + buffer[7]);
                    init();

                   
                    TTC = 0;
                    LLC = 0;

                    
                    pcb.setJobID(new char[] { buffer[4], buffer[5], buffer[6], buffer[7] });
                    pcb.setTTL(new char[] { buffer[8], buffer[9], buffer[10], buffer[11] });
                    pcb.setTLL(new char[] { buffer[12], buffer[13], buffer[14], buffer[15] });

                   
                    ttl = Integer.parseInt(String.valueOf(pcb.getTTL()));
                    tll = Integer.parseInt(String.valueOf(pcb.getTLL()));

                   
                    Map.Entry<Integer, int[]> pair = allocate(random);
                    PTR = pair.getKey() * 10; 
                    used_memory = PTR;
                    random = pair.getValue();

                    for (int i = PTR; i < (PTR + 10); i++) {
                        for (int j = 0; j < 4; j++) {
                            M[i][j] = '*';
                        }
                    }
                    continue;

                } else if (buffer[0] == '$' && buffer[1] == 'D' && buffer[2] == 'T' && buffer[3] == 'A') {
                    STARTEXECUTION();
                    continue;

                } else if (buffer[0] == '$' && buffer[1] == 'E' && buffer[2] == 'N' && buffer[3] == 'D') {
                    System.out.println("End Of card ");
                    continue;
                } else {
                    loadProgram(M, buffer);
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void loadProgram(char[][] memory, char[] buffer) {
        if (used_memory >= (PTR + 10)) {
            System.out.println("Memory is full");
        }

        Map.Entry<Integer, int[]> pair = allocate(random);

        int frameNumber = pair.getKey();
        random = pair.getValue();

      
        memory[used_memory][2] = (char) (frameNumber / 10 + '0');
        memory[used_memory][3] = (char) (frameNumber % 10 + '0');

        
        int framePtr = frameNumber * 10;
        int k = 0;
        for (int i = framePtr; i < (framePtr + 10) && k < buffer.length; i++) {
            for (int j = 0; j < 4 && k < buffer.length; j++) {
                memory[i][j] = buffer[k++];
            }
        }
        used_memory++;
    }

    private void STARTEXECUTION() {
        setIC(0);
        EXECUTEUSERPROGRAM(M);

    }

    private void EXECUTEUSERPROGRAM(char[][] memory) {
        while (true) {
            int RIC = addressMap(getIC());
            setIR(new char[] {
                    memory[RIC][0],
                    memory[RIC][1],
                    memory[RIC][2],
                    memory[RIC][3]

            });

            setIC(getIC() + 1); 
           
            if (getIR(0) != 'H' && (!Character.isDigit(getIR()[2]) || !Character.isDigit(getIR()[3]))) {
                setPI(2);
                MOS();
                break;
            }
            if (getIR(0) != 'H') {
                realAddress = addressMap(getOperand());
            }

            
            if (getPI() != 0 || (getTI() != 0 && getPI() != 0)) {
                MOS();
                if (!flag) {
                    flag = true;
                    return;
                }
                realAddress = addressMap(getOperand());
                setPI(0);
            }

            

            examine();

            if (isExceeded) {
                isExceeded = false;
                return;
            }
            if (reachedH) {
                reachedH = false;
                return;
            }
            if (getPI() != 0 || getTI() != 0) {
                MOS();
                return;
            }

            if (!flag) {
                flag = true;
                return;
            }

            SIMULATION();

        }

    }

    private void examine() {
        String opcode = getOpcode();
        switch (opcode) {
            case "LR": {

                setR(
                        new char[] {
                                M[realAddress][0],
                                M[realAddress][1],
                                M[realAddress][2],
                                M[realAddress][3]
                        });
            }
                break;
            case "SR": {
                char[] arr = getR();
                M[realAddress][0] = arr[0];
                M[realAddress][1] = arr[1];
                M[realAddress][2] = arr[2];
                M[realAddress][3] = arr[3];
            }
                break;
            case "CR": {
                if (getR()[0] == M[realAddress][0] &&
                        getR()[1] == M[realAddress][1] &&
                        getR()[2] == M[realAddress][2] &&
                        getR()[3] == M[realAddress][3]) {
                    setC(true);
                } else {
                    setC(false);
                }
            }
                break;
            case "BT": {
                if (getC())
                    setIC(getOperand());

            }
                break;
            case "GD": {
                setSI(1);
                MOS();

            }
                break;
            case "PD": {

                setSI(2);
                MOS();
            }
                break;
            case "H": {

                setSI(3);
                MOS();
                reachedH = true;
                return;
            }

            default: {
                setPI(1);

            }
        }

    }

    private void SIMULATION() {
        TTC++;
        if (TTC == ttl) {
            setTI(2);
        }
    }

    private int addressMap(int va) {
        int pte = PTR + va / 10;

        if (M[pte][2] != '*') {
            int realAddress = Integer.parseInt(String.valueOf(M[pte][2]) + String.valueOf(M[pte][3])) * 10 + va % 10;

            return realAddress;

        } else {
            setPI(3);
            return -1;
        }

    }

    protected int allocate() {
    
        int value = (int) (Math.random() * 30);
       
        while (true) {
            if (random[value] == 0) {
                random[value] = 1;
                break;
            } else {
                value = (int) (Math.random() * 30);
            }
        }

       
        return value;
    }

    private void WRITE() {
      
        LLC++;
       
        if (LLC > tll) {
            isExceeded = true;
            setTI(2);
            TERMINATE(2);
            return;
        }

        int oprand = getOperand();

     
        if (oprand % 10 != 0) {
            
            oprand = oprand - (oprand % 10);

        }

        for (int i = realAddress; i < realAddress + 10; i++) {
            for (int j = 0; j < 4; j++) {
                if (M[i][j] != '\0') {
                    try {
                        outputReader.write(M[i][j]);
                    } catch (Exception e) {

                    }

                }
            }
        }

        try {
            outputReader.newLine();
            outputReader.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
        setSI(0);
    }

    private void READ() {
        flag = true;
        String line = "";
        try {
            line = inputReader.readLine();
            if (line == null) {
               

            } else if (line.startsWith("$END")) {
                
                flag = false;
                TERMINATE(1);
                return;
            }
            char[] buffer = line.toCharArray();
            int oprand = getOperand();

           
            if (oprand % 10 != 0) {
             
                oprand = oprand - (oprand % 10);

            }
          
            for (int i = 0; i < line.length();) {
                M[realAddress][i % 4] = buffer[i];

                i++;
                if (i % 4 == 0) {
                    realAddress++;
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        setSI(0);

    }

    private void MOS() {
        switch ("" + getTI() + getSI()) {
            case "10": {
                READ();
            }
                break;

            case "02": {
                WRITE();

            }
                break;

            case "03": {
                setSI(0);
                TERMINATE(0);
            }
                break;

            case "21": {
                TERMINATE(3);
            }
                break;

            case "22": {
                isExceeded = true;
                WRITE();
                TERMINATE(3);

            }
                break;

            case "23": {
                TERMINATE(0);
            }
                break;

            case "20": {
                TERMINATE(3);
            }

        }

        switch ("" + getTI() + getPI()) {
            case "01": {

                TERMINATE(4);

            }
                break;

            case "02": {
                TERMINATE(5);

            }
                break;

            case "03": {

                if (getIR()[0] == 'G' || getIR()[0] == 'S') {
                    valid = 1;

                }
                if (valid == 1) {
                    valid = 0;
                    int al = allocate();
                    int ir = getIR(2) - '0';
                    M[PTR + ir][2] = (char) (al / 10 + '0');
                    M[PTR + ir][3] = (char) (al % 10 + '0');
                    setPI(0);
                } else {
                    flag = false;
                    TERMINATE(6);

                }
            }
                break;

            case "21":
                TERMINATE(7);
                break;

            case "22":
                TERMINATE(8);
                break;

            case "23":
                TERMINATE(3);
                break;

            case "default":
                TERMINATE(3);
                break;
        }

    }

    private void TERMINATE(int code) {

    
        printMemory();
      
        try {

            String line = getErrorMessage(code);
            outputReader.write(String.format("JOB ID   :  %s\n", pcb.getJobID()));
            outputReader.write(line);
            outputReader.write("\n");
            outputReader.write("IC       :  " + getIC() + "\n");
            if (String.valueOf(getIR()).charAt(0) == 'H') {
                outputReader.write(String.format("IR       :  %c\n", String.valueOf(getIR()).charAt(0)));                
            }
            else{
                outputReader.write("IR       :  " + String.valueOf(getIR()) + "\n");
            }
            outputReader.write("TTL      :  " + ttl + "\n");
            outputReader.write("TLL      :  " + tll + "\n");
            outputReader.write("TTC      :  " + TTC + "\n");
            outputReader.write("LLC      :  " + LLC + "\n");
            outputReader.write("\n\n");
            setSI(0);
            setTI(0);
            setPI(0);
         
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void close() {
        try {
            outputReader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String getErrorMessage(int code) {
        switch (code) {
            case 0: {
                TTC++;
                return " NO ERROR";
            }
            case 1: {
                return " OUT OF DATA";
            }
            case 2: {
                TTC++;
                LLC--;
                return " LINE LIMIT EXCEEDED";
            }
            case 3: {
                if (TI == 2 && PI == 1)
                {
                    return "TIME LIMIT EXCEEDED AND OPERATION CODE ERROR\n";
                }
                if (TI == 2 && PI == 2)
                {
                    return "TIME LIMIT EXCEEDED AND OPERAND ERROR\n";
                }

                return " TIME LIMIT EXCEEDED";
            }
            case 4: {
                return " OPERATION CODE ERROR";
            }
            case 5: {
                return " OPERAND ERROR";
            }
            case 6: {
                return " INVALID PAGE FAULT";
            }
            default: {
                return " INVALID ERROR";
            }
        }
    }
}

public class phase2 {

    public static void main(String[] args) {
        OperatingSystem os = new OperatingSystem("input_2.txt", "output_2.txt");
        os.init();
        os.printMemory();
        os.LOAD();
        os.close();
    }
}