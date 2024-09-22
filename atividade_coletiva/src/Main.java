public class Main {
    public static void main(String[] args) throws Exception {
        
        int[] threadConfigurations = {1, 2, 4, 8, 16, 32, 64, 80, 160, 320};
        Experiment experiment = new Experiment(1, false);
        experiment.runExperimentsWithDifferentThreads(threadConfigurations);

        Experiment experimentWithYearThreads = new Experiment(1, true);
        experimentWithYearThreads.runExperimentsWithDifferentThreads(threadConfigurations);

        
    }
}
