package service;

import java.io.Serializable;
import java.util.function.Predicate;

public class TTCFilter implements Predicate<String[]>, Serializable {
    public String getReg() {
        return reg;
    }

    public double getTemStart() {
        return temStart;
    }

    public double getTemStop() {
        return temStop;
    }

    public double getUncStart() {
        return uncStart;
    }

    public double getUncStop() {
        return uncStop;
    }

    public int getTimeStart() {
        return timeStart;
    }

    public int getTimeStop() {
        return timeStop;
    }

    String reg;
        double temStart, temStop;
        double uncStart, uncStop;
        int timeStart, timeStop;

        public TTCFilter(String reg, double temStart, double temStop, double uncStart, double uncStop, int timeStart, int timeStop) {
            this.reg = reg;
            this.temStart = temStart;
            this.temStop = temStop;
            this.uncStart = uncStart;
            this.uncStop = uncStop;
            this.timeStart = timeStart;
            this.timeStop = timeStop;
        }

        @Override
        public boolean test(String[] strings) {
            if (strings[1].length() == 0 || strings[2].length() == 0) return false;
            int time = Integer.parseInt(strings[0]);
            double tem = Double.parseDouble(strings[1]);
            double unc = Double.parseDouble(strings[2]);
            return timeStart <= time && time <= timeStop
                    && strings[3].contains(reg) && temStart <= tem && tem <= temStop && uncStart <= unc && unc <= uncStop;
        }
    }