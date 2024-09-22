import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class CityProcessor {
    private String cityName;
    private String filePath;

    public CityProcessor(String cityName, String filePath) {
        this.cityName = cityName;
        this.filePath = filePath;
    }

    // processa os dados da cidade 
    public void processCityData() {
        
        Map<String, double[]> monthlyData = new HashMap<>();

        // começa a leitura do arquivo CSV
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            boolean firstLine = true; 
            while ((line = br.readLine()) != null) {
                // Ignora primeira linha
                if (firstLine) {
                    firstLine = false;
                    continue;
                }

                String[] data = line.split(",");
                String month = data[4] + "-" + data[2]; 
                double temperature = Double.parseDouble(data[5]);

                
                monthlyData.computeIfAbsent(month, k -> new double[]{Double.MAX_VALUE, Double.MIN_VALUE, 0.0, 0.0});

                // Atualiza dados mensais
                double[] stats = monthlyData.get(month);
                stats[0] = Math.min(stats[0], temperature); 
                stats[1] = Math.max(stats[1], temperature); 
                stats[2] += temperature; 
                stats[3] += 1; 
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Impressão dos dados mensais
        for (Map.Entry<String, double[]> entry : monthlyData.entrySet()) {
            String month = entry.getKey();
            double[] stats = entry.getValue();
            double avgTemperature = stats[2] / stats[3]; 
            System.out.println("Cidade: " + cityName + " - Mês: " + month +
                    " - Min: " + stats[0] + "°C, Max: " + stats[1] + "°C, Média: " + avgTemperature + "°C");
        }
    }
}