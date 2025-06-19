// TrabalhadorTSP.java
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.List;

public class TrabalhadorTSP {

    public static void main(String[] args) {
        final String HOST_MESTRE = "localhost";
        final int PORTA_MESTRE = 65432;

        System.out.println("Conectando ao mestre em " + HOST_MESTRE + "...");
        try (
            Socket socket = new Socket(HOST_MESTRE, PORTA_MESTRE);
            ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
            ObjectInputStream in = new ObjectInputStream(socket.getInputStream())
        ) {
            System.out.println("Conectado! Aguardando trabalho...");

            @SuppressWarnings("unchecked")
            List<List<Cidade>> rotasParaCalcular = (List<List<Cidade>>) in.readObject();

            System.out.println("Trabalho recebido: " + rotasParaCalcular.size() + " rotas para analisar.");

            Resultado melhorResultadoLocal = encontrarMelhorRotaLocal(rotasParaCalcular);

            System.out.println("Cálculo concluído. Enviando melhor rota local de volta...");
            out.writeObject(melhorResultadoLocal);
            out.flush();

            System.out.println("Resultado enviado. Encerrando.");

        } catch (Exception e) {
            System.err.println("Erro durante execução do trabalhador:");
            e.printStackTrace();
        }
    }

    private static Resultado encontrarMelhorRotaLocal(List<List<Cidade>> rotas) {
        double distanciaMinima = Double.MAX_VALUE;
        List<Cidade> melhorRota = null;

        for (List<Cidade> rota : rotas) {
            double distanciaAtual = 0;
            for (int i = 0; i < rota.size() - 1; i++) {
                distanciaAtual += rota.get(i).distanciaPara(rota.get(i + 1));
            }

            if (distanciaAtual < distanciaMinima) {
                distanciaMinima = distanciaAtual;
                melhorRota = rota;
            }
        }
        return new Resultado(distanciaMinima, melhorRota);
    }
}
