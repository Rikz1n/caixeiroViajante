package paralelo;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

// Classe principal que orquestra a resolução paralela do Problema do Caixeiro Viajante (TSP).
public class CaixeiroViajanteParalelo {

    private final int[][] matrizDistancias;
    private final int numCidades;

    // Variáveis para armazenar a melhor solução encontrada de forma concorrente.
    private volatile double menorDistancia = Double.MAX_VALUE;
    private volatile List<Integer> melhorCaminho = new ArrayList<>();

    public CaixeiroViajanteParalelo(int[][] matrizDistancias) {
        this.matrizDistancias = matrizDistancias;
        this.numCidades = matrizDistancias.length;
    }

    // Método sincronizado para atualizar a menor distância encontrada.
    public synchronized void atualizarMelhorCaminho(double distancia, List<Integer> caminho) {
        if (distancia < menorDistancia) {
            menorDistancia = distancia;
            melhorCaminho = new ArrayList<>(caminho);
        }
    }

    // Resolve o TSP de forma paralela, distribuindo o trabalho entre threads.
    public void resolver() {
        long tempoInicial = System.currentTimeMillis();

        int numThreads = Runtime.getRuntime().availableProcessors();
        ExecutorService executor = Executors.newFixedThreadPool(numThreads);

        List<Integer> cidadesParaVisitar = IntStream.range(1, numCidades)
                                                     .boxed()
                                                     .collect(Collectors.toList());

        for(Integer proximaCidade : cidadesParaVisitar) {
            List<Integer> cidadesRestantes = new ArrayList<>(cidadesParaVisitar);
            cidadesRestantes.remove(proximaCidade);
            executor.submit(new TarefaTSP(proximaCidade, cidadesRestantes, this));
        }

        executor.shutdown();
        
        try {
            executor.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
        }
        catch(InterruptedException e) {
            System.err.println("Erro ao aguardar a finalização das threads.");
            Thread.currentThread().interrupt();
        }

        long tempoFinal = System.currentTimeMillis();

        imprimirResultado(tempoFinal - tempoInicial);
    }

    private void imprimirResultado(long duracao) {
        System.out.println("--- Execução Paralela Finalizada ---");
        System.out.println("Melhor caminho encontrado: " + melhorCaminho);
        System.out.println("Distância total: " + String.format("%.2f", menorDistancia));
        System.out.println("Tempo de execução: " + duracao + " ms");
    }

    public int[][] getMatrizDistancias() {
        return matrizDistancias;
    }
    
    public static void main(String[] args) {
        System.out.println("Resolvendo para o problema de 5 cidades...");
        int[][] matriz5 = DadosTSP.getMatriz5Cidades();
        CaixeiroViajanteParalelo tspSolver5 = new CaixeiroViajanteParalelo(matriz5);
        tspSolver5.resolver();

        System.out.println("\n---------------------------------------\n");
        
        
        System.out.println("Resolvendo para o problema de 7 cidades...");
        int[][] matriz7 = DadosTSP.getMatriz7Cidades();
        CaixeiroViajanteParalelo tspSolver7 = new CaixeiroViajanteParalelo(matriz7);
        tspSolver7.resolver();

        System.out.println("\n---------------------------------------\n");
        
        
        System.out.println("Resolvendo para o problema de 10 cidades (pode ser muito demorado)...");
        int[][] matriz10 = DadosTSP.getMatriz10Cidades();
        CaixeiroViajanteParalelo tspSolver10 = new CaixeiroViajanteParalelo(matriz10);
        tspSolver10.resolver();
        
    }
}