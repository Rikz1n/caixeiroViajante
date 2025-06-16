// Resultado.java
import java.io.Serializable;
import java.util.List;

public class Resultado implements Serializable {
    private static final long serialVersionUID = 1L;
    double distanciaMinima;
    List<Cidade> melhorRota;

    public Resultado(double distanciaMinima, List<Cidade> melhorRota) {
        this.distanciaMinima = distanciaMinima;
        this.melhorRota = melhorRota;
    }

    public double getDistanciaMinima() {
        return distanciaMinima;
    }

    public List<Cidade> getMelhorRota() {
        return melhorRota;
    }
}