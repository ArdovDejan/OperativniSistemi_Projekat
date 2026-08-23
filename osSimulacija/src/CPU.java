import java.util.List;

public class CPU {
    private PCB current;
    private long cycleCount;
    private MemoryManager memoryManager;

    public CPU(MemoryManager memoryManager) {
        this.current = null;
        this.cycleCount = 0;
        this.memoryManager=memoryManager;
    }

    public void executeOneStep() {
        if (current == null) return;

        List<String> instrukcije = current.getInstructions();

        if (instrukcije == null || instrukcije.isEmpty()) {
            current.setProgramCounter(current.getProgramCounter() + 1);
            cycleCount++;
            if (current.getProgramCounter() >= current.getBurstTime()) {
                current.setState(ProcessState.TERMINATED);
            }
            return;
        }

        int pc = current.getProgramCounter();

        if (pc >= instrukcije.size()) {
            current.setState(ProcessState.TERMINATED);
            return;
        }

        String instrukcija = instrukcije.get(pc);
        izvrsiInstrukciju(instrukcija);

        current.setProgramCounter(pc + 1);
        cycleCount++;
    }

    private void izvrsiInstrukciju(String instrukcija) {
        String[] ascode = instrukcija.split(" ");
        String code = ascode[0];

        switch (code) {
            case "01": // ADD
                int a = current.getRegisters().getOrDefault(ascode[1], 0);
                int b = current.getRegisters().getOrDefault(ascode[2], 0);
                current.getRegisters().put(ascode[1], a + b);
                System.out.println("[CPU] ADD: R" + ascode[1] + " = " + (a + b));
                break;

            case "02": // MOV
                int vrijednost = Integer.parseInt(ascode[2], 16);
                current.getRegisters().put(ascode[1], vrijednost);
                System.out.println("[CPU] MOV: R" + ascode[1] + " = " + vrijednost);
                break;

            case "03": // SUB
                int x = current.getRegisters().getOrDefault(ascode[1], 0);
                int y = current.getRegisters().getOrDefault(ascode[2], 0);
                current.getRegisters().put(ascode[1], x - y);
                System.out.println("[CPU] SUB: R" + ascode[1] + " = " + (x - y));
                break;

            case "04": // MUL
                int m1 = current.getRegisters().getOrDefault(ascode[1], 0);
                int m2 = current.getRegisters().getOrDefault(ascode[2], 0);
                current.getRegisters().put(ascode[1], m1 * m2);
                System.out.println("[CPU] MUL: R" + ascode[1] + " = " + (m1 * m2));
                break;

            case "06": // STORE - spremi registar u memoriju
                int adresa = Integer.parseInt(ascode[2], 16);
                int regVrijednost = current.getRegisters().getOrDefault(ascode[1], 0);
                memoryManager.write(current, adresa, regVrijednost);
                System.out.println("[CPU] STORE: memorija[" + adresa + "] = " + regVrijednost);
                break;

            case "05": // LOAD - ucitaj iz memorije u registar
                int loadAdresa = Integer.parseInt(ascode[2], 16);
                int loadVrijednost = memoryManager.read(current, loadAdresa);
                current.getRegisters().put(ascode[1], loadVrijednost);
                System.out.println("[CPU] LOAD: R" + ascode[1] + " = " + loadVrijednost);
                break;

            case "FF": // HLT
                System.out.println("[CPU] HLT: Proces zavrsava.");
                current.setState(ProcessState.TERMINATED);
                break;

            default:
                System.out.println("[CPU] Nepoznata instrukcija: " + ascode);
                break;
        }
    }

    public void contextSwitch(PCB next) {
        if (current != null) {
            current.setState(ProcessState.READY);
        }
        this.current = next;
        if (next != null) {
            next.setState(ProcessState.RUNNING);
        }
    }

    public PCB getCurrent() {
        return current;
    }


    public long getCycleCount() {
        return cycleCount;
    }

}
