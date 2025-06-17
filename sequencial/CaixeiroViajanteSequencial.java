package sequencial;

import java.util.ArrayList;
import java.util.List;

public class CaixeiroViajanteSequencial {

    static int[][] matriz5 = {
        {0, 2, 9, 10, 7},
        {2, 0, 6, 4, 3},
        {9, 6, 0, 8, 5},
        {10, 4, 8, 0, 6},
        {7, 3, 5, 6, 0}
    };

      static int[][] matriz7 = {
        {0, 29, 20, 21, 16, 31, 100},
        {29, 0, 15, 29, 28, 40, 72},
        {20, 15, 0, 15, 14, 25, 81},
        {21, 29, 15, 0, 4, 12, 92},
        {16, 28, 14, 4, 0, 16, 94},
        {31, 40, 25, 12, 16, 0, 95},
        {100, 72, 81, 92, 94, 95, 0}
    };

    public static Result tsp(int[][] matriz) {
        // Cria uma lista com as cidades (exceto a cidade inicial 0)
        List<Integer> vertices = new ArrayList<>();
        for (int i = 1; i < matriz.length; i++) {
            vertices.add(i);
        }

        // Variáveis para armazenar o menor custo e o melhor caminho
        int minCusto = Integer.MAX_VALUE;
        List<Integer> melhorCaminho = null;

        // Gera todas as permutações possíveis dos caminhos
        List<List<Integer>> permutacoes = gerarPermutacoes(vertices);

         // Para cada permutação, calcula o custo do caminho
        for (List<Integer> perm : permutacoes) {
            int custoAtual = 0;
            int k = 0;

            // Soma os custos entre cada cidade no caminho
            for (int i : perm) {
                custoAtual += matriz[k][i];
                k = i;
            }

            // Soma o custo de voltar para a cidade inicial (0)
            custoAtual += matriz[k][0]; // voltar para o início

            // Verifica se esse caminho tem um custo menor que o atual mínimo
            if (custoAtual < minCusto) {
                minCusto = custoAtual;
                melhorCaminho = perm;
            }
        }

        // Retorna o resultado com o menor custo e o melhor caminho encontrado
        return new Result(minCusto, melhorCaminho);
    }

    // Função para gerar permutações (ordens possíveis de cidades)
    public static List<List<Integer>> gerarPermutacoes(List<Integer> elementos) {
        if (elementos.isEmpty()) {
            List<List<Integer>> resultado = new ArrayList<>();
            resultado.add(new ArrayList<>());
            return resultado;
        }

        List<List<Integer>> resultado = new ArrayList<>();
        Integer primeiroElemento = elementos.get(0);
        List<Integer> restante = elementos.subList(1, elementos.size());

        for (List<Integer> perm : gerarPermutacoes(restante)) {
            for (int i = 0; i <= perm.size(); i++) {
                List<Integer> novaPerm = new ArrayList<>(perm);
                novaPerm.add(i, primeiroElemento);
                resultado.add(novaPerm);
            }
        }
        return resultado;
    }

    // Classe para armazenar o resultado
    static class Result {
        int custo;
        List<Integer> caminho;

        Result(int custo, List<Integer> caminho) {
            this.custo = custo;
            this.caminho = caminho;
        }
    }

    public static void main(String[] args) {
        // Rodando para matriz de 5 cidades
        long inicio5 = System.currentTimeMillis();
        Result resultado5 = tsp(matriz5);
        long fim5 = System.currentTimeMillis();

        System.out.println("=== Matriz de 5 cidades ===");
        System.out.println("Melhor caminho: " + caminhoFormatado(resultado5.caminho));
        System.out.println("Custo: " + resultado5.custo);
        System.out.println("Tempo: " + (fim5 - inicio5) + " ms\n");

        // Rodando para matriz de 7 cidades
        long inicio7 = System.currentTimeMillis();
        Result resultado7 = tsp(matriz7);
        long fim7 = System.currentTimeMillis();

        System.out.println("=== Matriz de 7 cidades ===");
        System.out.println("Melhor caminho: " + caminhoFormatado(resultado7.caminho));
        System.out.println("Custo: " + resultado7.custo);
        System.out.println("Tempo: " + (fim7 - inicio7) + " ms");
    }

    // Formata o caminho adicionando o ponto inicial e final (cidade 0)
    public static String caminhoFormatado(List<Integer> caminho) {
        StringBuilder sb = new StringBuilder();
        sb.append("0 -> "); // Começa na cidade 0
        for (int c : caminho) {
            sb.append(c).append(" -> ");
        }
        sb.append("0"); // Retorna para a cidade 0
        return sb.toString();
    }
}
