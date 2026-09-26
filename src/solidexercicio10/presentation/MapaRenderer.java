package solidexercicio10.presentation;

import solidexercicio10.model.Asteroide;
import solidexercicio10.model.Inimigo;
import solidexercicio10.model.Missao;
import solidexercicio10.model.Passageiro;

public class MapaRenderer {

    public void desenhar(Missao missao, int minX, int maxX, int minY, int maxY, int score, String pilotoNome) {
        System.out.println("\n================================================================");
        System.out.printf("Piloto: %s | Pontuação: %d%n", pilotoNome, score);
        System.out.println("================================================================");

        for (int y = maxY; y >= minY; y--) {
            for (int x = minX; x <= maxX; x++) {
                String simbolo = "."; 

                if (missao.getNave().getX() == x && missao.getNave().getY() == y) {
                    simbolo = missao.getNave().getSimbolo();
                } else {
                    for (Passageiro p : missao.getPassageiros()) {
                        if (p.getX() == x && p.getY() == y) {
                            simbolo = p.getSimbolo();
                            break;
                        }
                    }
                    
                    if (simbolo.equals(".")) {
                        for (Asteroide a : missao.getAsteroides()) {
                            if (a.getX() == x && a.getY() == y) {
                                simbolo = a.getSimbolo();
                                break;
                            }
                        }
                    }

                    if (simbolo.equals(".")) {
                        for (Inimigo i : missao.getInimigos()) {
                            if (i.getX() == x && i.getY() == y) {
                                simbolo = i.getSimbolo();
                                break;
                            }
                        }
                    }
                }
                System.out.print(simbolo + " ");
            }
            System.out.println();
        }
        System.out.println("================================================================\n");
    }
}