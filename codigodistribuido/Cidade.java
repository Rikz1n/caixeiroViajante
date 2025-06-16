import java.io.Serializable;

public class Cidade implements Serializable {
    private static final long serialVersionUID = 1L;
    String nome;
    int x;
    int y;

    public Cidade(String nome, int x, int y) {
        this.nome = nome;
        this.x = x;
        this.y = y;
    }

    public double distanciaPara(Cidade outra) {
        int distX = this.x - outra.x;
        int distY = this.y - outra.y;
        return Math.sqrt(distX * distX + distY * distY);
    }

    @Override
    public String toString() {
        return nome;
    }
}