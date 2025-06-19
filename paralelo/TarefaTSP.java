package paralelo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

// Contém a lógica de permutação recursiva para um subconjunto do problema.
public class TarefaTSP implements Runnable {
    private final CaixeiroViajanteParalelo solver;
    private final List<Integer> caminhoParcial;
    private final List<Integer> cidadesParaPermutar;

    public TarefaTSP(int segundaCidade, List<Integer> cidadesRestantes, CaixeiroViajanteParalelo solver) {
        this.solver = solver;
        this.caminhoParcial = new ArrayList<>();
        this.caminhoParcial.add(0); // Começa na cidade 0
        this.caminhoParcial.add(segundaCidade); // Adiciona a segunda cidade, que define esta tarefa
        this.cidadesParaPermutar = cidadesRestantes;
    }

    @Override
    public void run() {
        // Inicia a permutação recursiva para as cidades restantes.
        permutar(cidadesParaPermutar.size(), cidadesParaPermutar);
    }

    // Gera todas as permutações possíveis para a lista de cidades.
    private void permutar(int n, List<Integer> cidades) {
        // Caso base: se n=1, a permutação está completa.
        if(n == 1) {
            processarPermutacao(cidades);
            return;
        }

        for(int i = 0; i < n; i++) {
            // Troca o elemento atual com o último para gerar uma nova ordem.
            Collections.swap(cidades, i, n - 1);
            // Chama a recursão para o resto da lista.
            permutar(n - 1, cidades);
            // Desfaz a troca para restaurar a lista original.
            Collections.swap(cidades, i, n - 1);
        }
    }

    // Calcula a distância total de um caminho e o submete para verificação.
    private void processarPermutacao(List<Integer> permutacaoFinal) {
        List<Integer> caminhoCompleto = new ArrayList<>(caminhoParcial);
        caminhoCompleto.addAll(permutacaoFinal);

        double distanciaTotal = 0;
        int[][] matriz = solver.getMatrizDistancias();

        // Soma as distâncias entre as cidades consecutivas no caminho.
        for(int i = 0; i < caminhoCompleto.size() - 1; i++) {
            distanciaTotal += matriz[caminhoCompleto.get(i)][caminhoCompleto.get(i + 1)];
        }
        // Adiciona a distância de volta para a cidade de origem.
        distanciaTotal += matriz[caminhoCompleto.get(caminhoCompleto.size() - 1)][caminhoCompleto.get(0)];
        
        // Adiciona a cidade de origem ao final.
        caminhoCompleto.add(0);

        // Submete o resultado para o solver principal.
        solver.atualizarMelhorCaminho(distanciaTotal, caminhoCompleto);
    }
}