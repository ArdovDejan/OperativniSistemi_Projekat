import java.util.ArrayList;
import java.util.List;

public class Asembler {

    public List<String> compile(String asmCode) {
        List<String> rezultat = new ArrayList<>();

        String[] linije = asmCode.split("\n");

        for (String linija : linije) {
            linija = linija.trim();


            if (linija.isEmpty() || linija.startsWith(";")) {  // ";" je za komentare u asebler kodu
                continue;
            }

            String prevedena = prevediLiniju(linija);

            if (prevedena != null) {
                rezultat.add(prevedena);
                System.out.println("[Asembler] " + linija + " → " + prevedena);
            } else {
                System.out.println("[Asembler] GRESKA: Nepoznata instrukcija: " + linija);
            }
        }

        return rezultat;
    }

    private String prevediLiniju(String linija) {
        String[] dijelovi = linija.split("[,\\s]+");
        String instrukcija = dijelovi[0].toUpperCase();

        switch (instrukcija) {
            case "ADD":
                return String.format("%02X %02X %02X",
                        0x01,
                        prevediOperand(dijelovi[1]),
                        prevediOperand(dijelovi[2]));

            case "MOV":
                return String.format("%02X %02X %02X",
                        0x02,
                        prevediOperand(dijelovi[1]),
                        prevediOperand(dijelovi[2]));

            case "SUB":
                return String.format("%02X %02X %02X",
                        0x03,
                        prevediOperand(dijelovi[1]),
                        prevediOperand(dijelovi[2]));
            case "MUL":
                return String.format("%02X %02X %02X",
                        0x04,
                        prevediOperand(dijelovi[1]),
                        prevediOperand(dijelovi[2]));
            case "LOAD":
                return String.format("%02X %02X %02X",
                        0x05,
                        prevediOperand(dijelovi[1]),
                        prevediOperand(dijelovi[2]));
            case "STORE":
                return String.format("%02X %02X %02X",
                        0x06,
                        prevediOperand(dijelovi[1]),
                        prevediOperand(dijelovi[2]));
            case "HLT":
                return String.format("%02X 00 00", 0xFF);
            default:
                return null;
        }
    }

    private int prevediOperand(String operand) {
        operand = operand.trim().toUpperCase();
        switch (operand) {
            case "R1": return 0x01;
            case "R2": return 0x02;
            case "R3": return 0x03;
            case "R4": return 0x04;
            default:
                try {
                    return Integer.parseInt(operand);
                } catch (NumberFormatException e) {
                    System.out.println("[Asembler] Nepoznat operand: " + operand);
                    return 0x00;
                }
        }
    }

    public String compileToString(String asmCode) {
        List<String> instrukcije = compile(asmCode);
        return String.join("\n", instrukcije);
    }
}