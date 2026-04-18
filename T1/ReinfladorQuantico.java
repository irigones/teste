import java.io.IOException;
import java.math.BigInteger;
import java.nio.file.*;
import java.util.*;
import java.util.stream.Stream;

public class ReinfladorQuantico {

    public static void main(String[] args) {
        // Altere para a pasta onde estão os arquivos .txt extraídos do zip
        String pasta = "C:\\Users\\Fernanda\\VSCODE PUCRS\\ALEST II\\T1\\casos_11";

        try (Stream<Path> arquivos = Files.list(Paths.get(pasta))) {
            arquivos
                .filter(p -> p.toString().endsWith(".txt"))
                .sorted()
                .forEach(ReinfladorQuantico::processarArquivo);
        } catch (IOException e) {
            System.out.println("Erro ao acessar a pasta: " + e.getMessage());
        }
    }

    private static void processarArquivo(Path caminho) {
        try {
            List<String> linhas = Files.readAllLines(caminho);

            Map<Character, String> regras = new HashMap<>();
            Set<Character> esquerda = new HashSet<>();
            Set<Character> direita = new HashSet<>();

            for (String linha : linhas) {
                linha = linha.trim();

                if (linha.isEmpty()) {
                    continue;
                }

                char letra = linha.charAt(0);
                esquerda.add(letra);

                String substituicao = "";
                if (linha.length() > 1) {
                    substituicao = linha.substring(1).trim();
                }

                regras.put(letra, substituicao);

                for (int i = 0; i < substituicao.length(); i++) {
                    direita.add(substituicao.charAt(i));
                }
            }

            Character inicial = encontrarInicial(esquerda, direita);

            if (inicial == null) {
                System.out.println("Arquivo: " + caminho.getFileName());
                System.out.println("Não foi possível determinar a letra inicial.");
                System.out.println();
                return;
            }

            Map<Character, BigInteger> memo = new HashMap<>();
            Set<Character> visitando = new HashSet<>();

            BigInteger resultado = tamanhoFinal(inicial, regras, memo, visitando);

            System.out.println("Arquivo: " + caminho.getFileName());
            System.out.println("Letra inicial: " + inicial);
            System.out.println("Tamanho final: " + resultado);
            System.out.println();

        } catch (IOException e) {
            System.out.println("Erro ao ler arquivo " + caminho.getFileName() + ": " + e.getMessage());
        } catch (IllegalStateException e) {
            System.out.println("Arquivo: " + caminho.getFileName());
            System.out.println("Erro: " + e.getMessage());
            System.out.println();
        }
    }

    private static Character encontrarInicial(Set<Character> esquerda, Set<Character> direita) {
        for (char c : esquerda) {
            if (!direita.contains(c)) {
                return c;
            }
        }
        return null;
    }

    private static BigInteger tamanhoFinal(char letra,
                                           Map<Character, String> regras,
                                           Map<Character, BigInteger> memo,
                                           Set<Character> visitando) {

        if (memo.containsKey(letra)) {
            return memo.get(letra);
        }

        if (visitando.contains(letra)) {
            throw new IllegalStateException("Foi encontrado um ciclo envolvendo a letra '" + letra + "'.");
        }

        visitando.add(letra);

        String substituicao = regras.get(letra);

        BigInteger total;

        // Se não existe regra ou a regra é vazia, a letra permanece no texto final
        if (substituicao == null || substituicao.isEmpty()) {
            total = BigInteger.ONE;
        } else {
            total = BigInteger.ZERO;
            for (int i = 0; i < substituicao.length(); i++) {
                char filho = substituicao.charAt(i);
                total = total.add(tamanhoFinal(filho, regras, memo, visitando));
            }
        }

        visitando.remove(letra);
        memo.put(letra, total);

        return total;
    }
}