import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MestreTSP {

    public static void main(String[] args) {

        final int PORTA = 65432;
        final int NUM_TRABALHADORES = 2;
        
        List<Cidade> cidades = new ArrayList<>();
        cidades.add(new Cidade("A", 60, 200));
        cidades.add(new Cidade("B", 180, 200));
        cidades.add(new Cidade("C", 80, 180));
        cidades.add(new Cidade("D", 140, 180));
        cidades.add(new Cidade("E", 20, 160));
        cidades.add(new Cidade("F", 100, 160));
        cidades.add(new Cidade("G", 200, 160));
        cidades.add(new Cidade("H", 140, 140));

        try (ServerSocket servidorSocket = new ServerSocket(PORTA)) {
            System.out.println("Mestre ouvindo na porta " + PORTA);
            System.out.println("Aguardando " + NUM_TRABALHADORES + " trabalhadores...");

            List<Socket> trabalhadores = new ArrayList<>();
            while (trabalhadores.size() < NUM_TRABALHADORES) {
                trabalhadores.add(servidorSocket.accept());
                System.out.println("Trabalhador conectado: " + (trabalhadores.size()));
            }
            
            long tempoInicio = System.currentTimeMillis();

            System.out.println("Gerando todas as rotas possíveis...");
            List<List<Cidade>> todasAsRotas = gerarPermutacoes(new ArrayList<>(cidades.subList(1, cidades.size())));
            System.out.println("Total de " + todasAsRotas.size() + " rotas geradas.");

            Cidade cidadeInicial = cidades.get(0);
            for(List<Cidade> rota : todasAsRotas) {
                rota.add(0, cidadeInicial);
                rota.add(cidadeInicial);
            }
            
            int tamanhoLote = todasAsRotas.size() / NUM_TRABALHADORES;
            List<Thread> threads = new ArrayList<>();
            Resultado[] resultados = new Resultado[NUM_TRABALHADORES];

            for (int i = 0; i < NUM_TRABALHADORES; i++) {
                int de = i * tamanhoLote;
                int ate = (i == NUM_TRABALHADORES - 1) ? todasAsRotas.size() : de + tamanhoLote;
                
                List<List<Cidade>> lote = new ArrayList<>(todasAsRotas.subList(de, ate));

                Socket socketTrabalhador = trabalhadores.get(i);
                final int index = i;

                Thread thread = new Thread(() -> {
                    try (
                        ObjectOutputStream out = new ObjectOutputStream(socketTrabalhador.getOutputStream());
                        ObjectInputStream in = new ObjectInputStream(socketTrabalhador.getInputStream())
                    ) {
                        System.out.println("Enviando " + lote.size() + " rotas para o trabalhador " + (index + 1));
                        out.writeObject(lote);
                        out.flush();

                        resultados[index] = (Resultado) in.readObject();
                        System.out.println("Resultado recebido do trabalhador " + (index + 1));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });
                threads.add(thread);
                thread.start();
            }

            for(Thread t : threads) t.join();

            Resultado melhorResultadoGlobal = null;
            for (Resultado res : resultados) {
                if (res != null && (melhorResultadoGlobal == null || res.getDistanciaMinima() < melhorResultadoGlobal.getDistanciaMinima())) {
                    melhorResultadoGlobal = res;
                }
            }

            long tempoFim = System.currentTimeMillis();

            System.out.println("\n--- Resultado Final ---");
            if (melhorResultadoGlobal != null) {
                System.out.printf("Melhor Rota: %s\n", melhorResultadoGlobal.getMelhorRota());
                System.out.printf("Distância Mínima: %.2f\n", melhorResultadoGlobal.getDistanciaMinima());
            } else {
                System.out.println("Não foi possível determinar a melhor rota devido a erros na comunicação.");
            }
            System.out.printf("Tempo Total: %.4f segundos\n", (tempoFim - tempoInicio) / 1000.0);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private static List<List<Cidade>> gerarPermutacoes(List<Cidade> cidades) {
        List<List<Cidade>> permutacoes = new ArrayList<>();
        gerar(cidades.size(), cidades, permutacoes);
        return permutacoes;
    }

    private static void gerar(int k, List<Cidade> cidades, List<List<Cidade>> permutacoes) {
        if (k == 1) {
            permutacoes.add(new ArrayList<>(cidades));
        } else {
            gerar(k - 1, cidades, permutacoes);
            for (int i = 0; i < k - 1; i++) {
                if (k % 2 == 0) {
                    Collections.swap(cidades, i, k - 1);
                } else {
                    Collections.swap(cidades, 0, k - 1);
                }
                gerar(k - 1, cidades, permutacoes);
            }
        }
    }
}