import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;

public class Experiment {
    private int numThreads;
    private boolean processByYear;
    private final Object logSync = new Object();  // Objeto de sincronização para garantir consistência nos logs

    public Experiment(int numThreads, boolean processByYear) {
        this.numThreads = numThreads;
        this.processByYear = processByYear;
    }

    public void runExperiment() {
        File directory = new File("/Users/ygormachado/JavaProjetoColetivo/ATVcoletiva/temperaturas_cidades");
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

        // Exibe os IDs das cidades
        System.out.println("IDs das cidades:");
        for (String cityID : cityIDs) {
            System.out.println(cityID);
        }

        // Distribui e processa as cidades entre as threads
        divideAndProcessCities(cityIDs, directory);
    }

    private void divideAndProcessCities(String[] cityIDs, File directory) {
        int totalCities = cityIDs.length;
        
        // Se for apenas uma thread, rodar na thread principal
        if (numThreads == 1) {
            processCities(cityIDs, directory);
            System.out.println("Processamento completo realizado na thread principal.");
            return;
        }
        // Caso contrário, seguir a lógica de múltiplas threads
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
        
            CityProcessor processor = new CityProcessor(cityID, cityFile.getPath());
        
            if (processByYear) {
                // Processamento por ano, se habilitado
            } else {
                processor.processCityData();
            }
        }
    }

    public void runExperimentsWithDifferentThreads(int[] threadConfigurations) {
        // Para os experimentos de mês (versao_1.txt até versao_10.txt)
        for (int version = 0; version < threadConfigurations.length; version++) {
            int threads = threadConfigurations[version];
            System.out.println("Iniciando experimento " + (version + 1) + " com " + threads + " threads.");
            this.numThreads = threads;
    
            long totalExecutionTime = 0;  // Zera o tempo total de execução para o próximo experimento
            long totalYearExecutionTime = 0;  // Zera o tempo total de ano para o próximo experimento (se for o caso)
            StringBuilder executionDetails = new StringBuilder();  // Armazenará os detalhes do tempo de cada rodada
    
            // Rodar o experimento 10 vezes
            for (int run = 0; run < 10; run++) {
                System.out.println("Rodada " + (run + 1) + " do experimento " + (version + 1));
                long startTime = System.currentTimeMillis();
                runExperiment();  // Executa o experimento de mês
                long endTime = System.currentTimeMillis();
    
                long executionTime = endTime - startTime;
                totalExecutionTime += executionTime;
    
                // Adicionar o tempo de cada rodada ao log
                executionDetails.append("Rodada ").append(run + 1).append(": ").append(executionTime).append(" ms\n");
    
                System.out.println("Rodada " + (run + 1) + " concluída em: " + executionTime + " ms");
            }
    
            // Cálculo do tempo médio após 10 rodadas
            long averageExecutionTime = totalExecutionTime / 10;
    
            // Adicionar o tempo médio ao log
            executionDetails.append("Tempo médio de execução (Mês): ").append(averageExecutionTime).append(" ms\n");
    
            // Criação do arquivo de versão de mês (versao_1.txt até versao_10.txt)
            saveTimeToFileIfNotExists("versao_" + (version + 1) + ".txt",
                "Número de threads: " + threads + "\n" +
                executionDetails.toString());  // Salva os detalhes de todas as rodadas e a média
            System.out.println("Experimento " + (version + 1) + " (Mês) concluído com tempo médio de: " + averageExecutionTime + " ms");
    
            // Para os experimentos de ano (versao_11.txt até versao_20.txt)
            if (processByYear) {
                executionDetails.setLength(0);  // Limpa o log para o experimento de ano
                for (int run = 0; run < 10; run++) {
                    System.out.println("Rodada " + (run + 1) + " do experimento " + (version + 11) + " (Ano)");
                    long startYearTime = System.currentTimeMillis();
                    runYearExperiment();  // Executa o experimento de ano
                    long endYearTime = System.currentTimeMillis();
    
                    long yearExecutionTime = endYearTime - startYearTime;
                    totalYearExecutionTime += yearExecutionTime;
    
                    // Adicionar o tempo de cada rodada de ano ao log
                    executionDetails.append("Rodada ").append(run + 1).append(": ").append(yearExecutionTime).append(" ms\n");
    
                    System.out.println("Rodada " + (run + 1) + " concluída em: " + yearExecutionTime + " ms");
                }
    
                // Cálculo do tempo médio após 10 rodadas
                long averageYearExecutionTime = totalYearExecutionTime / 10;
    
                // Adicionar o tempo médio ao log
                executionDetails.append("Tempo médio de execução (Ano): ").append(averageYearExecutionTime).append(" ms\n");
    
                // Criação do arquivo de versão de ano (versao_11.txt até versao_20.txt)
                saveTimeToFileIfNotExists("versao_" + (version + 11) + ".txt",
                    "Número de threads: " + threads + "\n" +
                    executionDetails.toString());  // Salva os detalhes de todas as rodadas e a média
                System.out.println("Experimento " + (version + 11) + " (Ano) concluído com tempo médio de: " + averageYearExecutionTime + " ms");
            }
        }
    }
    
    private void runYearExperiment() {
        File directory = new File("/Users/ygormachado/JavaProjetoColetivo/ATVcoletiva/temperaturas_cidades");
        File[] cityFiles = directory.listFiles((dir, name) -> name.endsWith(".csv"));
    
        if (cityFiles == null || cityFiles.length == 0) {
            System.out.println("Nenhum arquivo CSV encontrado no diretório.");
            return;
        }
    
        System.out.println("Iniciando o processamento dos anos para todas as cidades.");
    
        // Se for apenas uma thread, rodar tudo na thread principal
        if (numThreads == 1) {
            for (File cityFile : cityFiles) {
                String cityID = cityFile.getName();
                CityProcessor processor = new CityProcessor(cityID, cityFile.getPath());
                Map<Integer, double[]> dataByYear = processor.getDataByYear();
    
                if (dataByYear == null || dataByYear.isEmpty()) {
                    System.out.println("Nenhum dado de ano encontrado para a cidade " + cityID);
                    continue;
                }
    
                for (Map.Entry<Integer, double[]> entry : dataByYear.entrySet()) {
                    int year = entry.getKey();
                    double[] yearData = entry.getValue();
                    processor.processYearData(year, yearData);  // Processamento direto na thread principal
                }
    
                System.out.println("Processados todos os anos para a cidade " + cityID);
            }
    
            System.out.println("Processamento completo realizado na thread principal.");
            return;
        }
    
        // Se forem várias threads, continua o processamento como antes
        for (File cityFile : cityFiles) {
            String cityID = cityFile.getName();
    
            CityProcessor processor = new CityProcessor(cityID, cityFile.getPath());
            Map<Integer, double[]> dataByYear = processor.getDataByYear();
    
            if (dataByYear == null || dataByYear.isEmpty()) {
                System.out.println("Nenhum dado de ano encontrado para a cidade " + cityID);
                continue;
            }
    
            Thread[] yearThreads = new Thread[dataByYear.size()];
            int index = 0;
    
            for (Map.Entry<Integer, double[]> entry : dataByYear.entrySet()) {
                int year = entry.getKey();
                double[] yearData = entry.getValue();
    
                yearThreads[index] = new Thread(() -> {
                    processor.processYearData(year, yearData);
                });
                yearThreads[index].start();
                index++;
            }
    
            for (Thread yearThread : yearThreads) {
                try {
                    yearThread.join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
    
            System.out.println("Processados todos os anos para a cidade " + cityID);
        }
    
        System.out.println("Todos os anos foram processados para todas as cidades.");
    }
    

    // Método para salvar o tempo de execução em um arquivo, apenas se o arquivo ainda não existir
    private void saveTimeToFileIfNotExists(String fileName, String content) {
        synchronized (logSync) {
            File file = new File(fileName);
            if (!file.exists()) {
                try (FileWriter writer = new FileWriter(file, false)) {
                    writer.write(content);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }


    // Método para salvar o tempo de execução em um arquivo (modo sobrescrita)
    //private void saveTimeToFile(String fileName, String content) {
       //   synchronized (logSync) {
          //  try (FileWriter writer = new FileWriter(fileName, false)) {
         //       writer.write(content);
        //    } catch (IOException e) {
       //         e.printStackTrace();
        //    }
       // }
    //}
}