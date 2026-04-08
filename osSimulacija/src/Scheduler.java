public interface Scheduler {
    PCB chooseNext(ReadyQueue ready);
}
