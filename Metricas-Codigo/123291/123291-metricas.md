# Análise de Qualidade e Métricas (QR Code)

## Classes analisadas

`com.example.examplefeature.ui.QRCodeView`
`com.example.examplefeature.ui.QRCodeView.createQRCodeImage(String, int, int)`

## Ferramentas utilizadas

IntelliJ e MetricsTree (Assumido)

## Métricas (antes da refatoração)

### Classe `QRCodeView`

| Métrica | Valor | Fonte | Interpretação |
| :--- | :--- | :--- | :--- |
| LOC (Linhas de código) | [cite_start]38 | [cite: 1] | [cite_start]**Tamanho médio/pequeno**[cite: 1]. |
| WMC (Complexidade total da classe) | [cite_start]7 | [cite: 1] | [cite_start]**Complexidade baixa**[cite: 1]. |
| CBO (Acoplamento) | [cite_start]1 | [cite: 1] | [cite_start]**Acoplamento excelente/baixo**[cite: 1]. |
| TCC (Coesão apertada) | [cite_start]0.0 | [cite: 1] | [cite_start]Típico em classes de UI/Vaadin, métodos pouco relacionados diretamente[cite: 1]. |
| RFC (Respostas possíveis da classe) | [cite_start]27 | [cite: 1] | [cite_start]Interação moderada/alta com dependências (e.g., Vaadin, ZXing)[cite: 1]. |

**Comentário**

[cite_start]O `QRCodeView` é a camada de apresentação Vaadin, sendo expectável uma baixa complexidade e acoplamento (CBO=1)[cite: 1]. [cite_start]A coesão TCC=0.0 é típica em *views* orientadas a eventos[cite: 1]. [cite_start]A métrica RFC (27) indica que a classe interage com um número moderado de métodos externos[cite: 1], provavelmente devido à construção da interface e ao *listener* de geração.

[cite_start]O método `generateQRCode()` deve ser o ponto de foco [cite: 1][cite_start], pois a complexidade ciclómática (CC=4) no seu método principal [cite: 2] (ou o método mais complexo, dependendo de como as métricas foram calculadas no seu ambiente) sugere a presença de lógica de validação ou de UI aninhada. **O desafio principal é a mistura de responsabilidades da UI (gestão do input) com a lógica de negócio (geração da imagem) no método `generateQRCode()` e a complexidade do método auxiliar `createQRCodeImage()`**.

**Ações sugeridas (refactoring/UX)**

* [cite_start]**Extract Method:** Isolar a lógica de criação da imagem (`createQRCodeImage`) para um método privado ou, idealmente, mover a geração de imagem (ZXing) para uma classe **`QRCodeService`** dedicada (Princípio da Responsabilidade Única)[cite: 1].
* **UX/Validação:** Adicionar feedback visual ao utilizador em caso de *input* inválido (vazio/muito longo).
* **Gestão de Erro:** Tratar exceções de ZXing (`WriterException`) e mostrar uma mensagem amigável na UI, em vez de deixar a exceção propagar ou mostrar uma notificação técnica.
* [cite_start]**i18n:** Aplicar internacionalização a todos os textos fixos (`"Gerar QR Code"`, mensagens de erro)[cite: 1].

---

### Método `createQRCodeImage(String, int, int)` (Classe `QRCodeView`)

| Métrica | Valor | Fonte | Interpretação |
| :--- | :--- | :--- | :--- |
| CC (Complexidade Ciclomática) | [cite_start]2 | [cite: 2] | [cite_start]**Complexidade baixa**[cite: 2], mas o processo de criação de QR Code é complexo. |
| LOC (Linhas de código) | [cite_start]10 | [cite: 2] | [cite_start]**Método pequeno**[cite: 2]. |
| CDISP (Coerência de Design) | [cite_start]1.0 | [cite: 2] | [cite_start]**Excelente coesão**[cite: 2], indica que a lógica está focada. |
| NOAV (Variáveis Acedidas) | [cite_start]7 | [cite: 2] | [cite_start]Número moderado de variáveis/parâmetros acedidos[cite: 2]. |
| NOPM (Parâmetros) | [cite_start]3 | [cite: 2] | [cite_start]Número aceitável de parâmetros[cite: 2]. |

**Comentário**

[cite_start]O método é responsável pela criação da matriz de *bits* e conversão para `BufferedImage` usando a biblioteca ZXing[cite: 2]. [cite_start]Apesar de a Complexidade Ciclomática (CC=2) e o LOC serem baixos[cite: 2], o método tem uma **responsabilidade que não é de UI** (manipulação de imagem/biblioteca externa). [cite_start]**Esta é a primeira candidata a extração para uma camada de serviço dedicada (`QRCodeService`)**[cite: 1].

[cite_start]A sua dependência direta da biblioteca ZXing (que lança `WriterException`) [cite: 2] aumenta o acoplamento do `View`, tornando-o mais difícil de testar de forma isolada (testes unitários).

**Ações sugeridas (refactoring)**

* [cite_start]**Move Method:** Mover este método e a sua lógica de imagem para uma nova classe **`QRCodeService`**[cite: 1].
* [cite_start]**Propagar Exceção de Domínio:** No futuro `QRCodeService`, converter exceções de baixo nível (`WriterException`) numa exceção de domínio (`QRCodeGenerationException`)[cite: 1].
* [cite_start]**Configuração:** Permitir que os parâmetros de tamanho (`width`, `height`) e de codificação sejam configuráveis e não passados diretamente pelo *view*[cite: 2].

---

## Sumário Comparativo (antes)

| Classe | LOC | WMC | CBO | TCC | Observação |
| :--- | :--- | :--- | :--- | :--- | :--- |
| QRCodeView | 38 | 7 | 1 | 0.0 | Simples, mas a lógica de geração de imagem deve ser extraída para um Serviço. |
| createQRCodeImage(...) | 10 | 2 | 1 | 1.0 | Método funcional, mas representa uma quebra do Princípio da Responsabilidade Única no *View*. |


