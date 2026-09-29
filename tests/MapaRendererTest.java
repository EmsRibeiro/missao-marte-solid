import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import solidexercicio10.model.Missao;
import solidexercicio10.model.Nave;
import solidexercicio10.model.Professor;
import solidexercicio10.presentation.MapaRenderer;

public class MapaRendererTest {
    public static void main(String[] args) {
        Nave nave = new Nave("A-1", 5);
        nave.moverComLimites('w', -1, 1, -1, 1);
        Missao missao = new Missao(nave);
        missao.adicionarPassageiro(new Professor("Dr. Silva", 1, 1));
        var bytes = new ByteArrayOutputStream();
        PrintStream anterior = System.out;
        try (var saida = new PrintStream(bytes, true, StandardCharsets.UTF_8)) {
            System.setOut(saida);
            new MapaRenderer().desenhar(missao, -1, 1, -1, 1, 19, "Paulo");
        } finally {
            System.setOut(anterior);
        }
        String mapa = bytes.toString(StandardCharsets.UTF_8);
        if (!mapa.contains("  -1    .   N   .") || !mapa.contains("   0    .   L   .")) {
            throw new AssertionError("w deve colocar nave acima da plataforma visível");
        }
        if (!mapa.contains("Dr. Silva (Professor) em (1,1)")) {
            throw new AssertionError("passageiros precisam ter tipo e posição identificáveis");
        }
        if (nave.getX() != 0 || nave.getY() != -1 || missao.getPassageiros().size() != 1) {
            throw new AssertionError("renderização não pode mudar a missão");
        }
        System.out.println("OK: orientação WASD, plataforma, identificação de passageiros e renderização sem mutação");
    }
}
