import java.io.File;

public class Experiment {
    private int numThreads;
    private boolean processByYear;

    public Experiment(int numThreads, boolean processByYear) {
        this.numThreads = numThreads;
        this.processByYear = processByYear;
    }

    public void runExperiment() {
        File directory = new File("temperaturas_cidades"); // se necessário alterar pelo seu path que contém os arquivos CSV.
        File[] cityFiles = directory.listFiles((dir, name) -> name.endsWith(".csv"));

        if (cityFiles == null || cityFiles.length == 0) {
            System.out.println("Nenhum arquivo CSV encontrado no diretório.");
            return;
        }

        // Coleta os IDs das cidades (nomes dos arquivos CSV)
        String[] cityIDs = new String[cityFiles.length];
        for (int i = 0; i < cityFiles.length; i++) {
            cityIDs[i] = cityFiles[i].getName();
        }

        // Distribui e processa as cidades entre as threads
        divideAndProcessCities(cityIDs, directory);
    }

    private void divideAndProcessCities(String[] cityIDs, File directory) {
        int totalCities = cityIDs.length;
        Thread[] threads = new Thread[numThreads];
        int citiesPerThread = Math.max(1, totalCities / numThreads);

        for (int i = 0; i < numThreads; i++) {
            int startCityIndex = i * citiesPerThread;
            int endCityIndex = Math.min(startCityIndex + citiesPerThread, totalCities);

            if (i == numThreads - 1) {
                endCityIndex = totalCities;
            }

            String[] citiesForThread = new String[endCityIndex - startCityIndex];
            System.arraycopy(cityIDs, startCityIndex, citiesForThread, 0, endCityIndex - startCityIndex);

            threads[i] = new Thread(() -> {
                processCities(citiesForThread, directory);
            });
            threads[i].start();
        }

        for (Thread thread : threads) {
            try {
                thread.join();
                System.out.println("Thread " + thread.getId() + " finalizada.");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        System.out.println("Todas as cidades neste lote foram processadas.");
    }

    private void processCities(String[] cityIDs, File directory) {
        for (String cityID : cityIDs) {
            File cityFile = new File(directory, cityID);

            if (!cityFile.exists()) {
                continue;
            }

            synchronized (this) {
                if (processByYear) {
                    // Processamento por ano, se habilitado
                } else {
                    // Processar cidades
                }
            }
        }
    }

    public void runExperimentsWithDifferentThreads(int[] threadConfigurations) {
        long totalExecutionTime = 0;
        long totalYearExecutionTime = 0;
    
        // Para os experimentos de mês (versao_1.txt até versao_10.txt)
        for (int version = 0; version < threadConfigurations.length; version++) {
            int threads = threadConfigurations[version];
            System.out.println("Iniciando experimento " + (version + 1) + " com " + threads + " threads.");
            this.numThreads = threads;
    
            // Tempo de execução para o mês
            long startTime = System.currentTimeMillis();
            runExperiment();  // Executa o experimento de mês
            long endTime = System.currentTimeMillis();
    
            long executionTime = endTime - startTime;
            totalExecutionTime += executionTime;
    
            // Criação do arquivo de versão de mês se o arquivo ainda não existir
            System.out.println("Experimento " + (version + 1) + " (Mês) concluído em: " + executionTime + " ms");
    
            // Para os experimentos de ano
            if (processByYear) {
                long startYearTime = System.currentTimeMillis();
                // Executar o experimento de ano
                long endYearTime = System.currentTimeMillis();
    
                long yearExecutionTime = endYearTime - startYearTime;
                totalYearExecutionTime += yearExecutionTime;
    
                // Criação do arquivo de versão de ano se o arquivo ainda não existir.
                System.out.println("Experimento " + (version + 11) + " (Ano) concluído em: " + yearExecutionTime + " ms");
            }
        }
    
        // Cálculo do médio dos meses.
         long averageExecutionTime = totalExecutionTime / 10;
         System.out.println(averageExecutionTime);
        // Criar o arquivo de tempo médio.
    
        // Cálculo do tempo médio dos anos.
        if (processByYear) {
            long averageYearExecutionTime = totalYearExecutionTime / 10;
            System.out.println(averageYearExecutionTime);
            // Criar arquivo do tempo médio dos anos.
        }
    }
}