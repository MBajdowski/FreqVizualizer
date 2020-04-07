package com.mbajdowski.utils;

public class ProgressPrinter {
    private String title;
    private int nrOfProgressSteps;
    private double noOfIterations;
    private int currentIteration;

    public ProgressPrinter(String title, int nrOfProgressSteps, int noOfIterations) {
        this.title = title;
        this.nrOfProgressSteps = nrOfProgressSteps;
        this.noOfIterations = noOfIterations;
        this.currentIteration = 0;

        System.out.println(System.lineSeparator()+title);
    }

    public void resetCurrentIteration(){
        this.currentIteration = 0;
    }

    public void incrementAndPrint(){
        currentIteration++;

        int percent = (int)(Math.ceil(currentIteration*100/this.noOfIterations));
        int currentSections = percent/(100/this.nrOfProgressSteps);
        StringBuilder sb = new StringBuilder();
        sb.append("[");
        for (int i = 0; i < currentSections; i++) {
            sb.append("#");
        }
        for (int i = currentSections; i < nrOfProgressSteps; i++) {
            sb.append("_");
        }
        sb.append("] ").append(percent).append("%\r");

        System.out.print(sb.toString());
    }
}
