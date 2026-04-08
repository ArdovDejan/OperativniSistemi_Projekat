public class HRRNScheduler implements Scheduler{
    private int timeQuantum;

    public HRRNScheduler(int timeQuantum) {
        this.timeQuantum = timeQuantum;
    }


    @Override
    public PCB chooseNext(ReadyQueue ready) {
        if(ready.isEmpty())
            return null;

        PCB best = null;
        double bestRatio = -1;

        for (PCB p : ready.getQueue())
        {
            double w = p.getWaitingTime();
            double s = p.getBurstTime();
            double ratio = (w + s)/s;

            if(ratio > bestRatio)
            {
                bestRatio =ratio;
                best=p;
            }
        }

        return best;

    }

    public int getTimeQuantum() {
        return timeQuantum;
    }
}
