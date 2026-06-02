<img height="177" alt="logo-minecraft" src="https://github.com/user-attachments/assets/bd2945b0-7c4c-4840-8108-46bd3d894ae8" />

> Versículo chave: "Consagre ao Senhor tudo o que você faz, e os seus planos serão bem-sucedidos." - Provérbios 16:3

## Legacy Mods port for Minecraft Java Edition (1.7.10 - 26.1.2)
<a href="https://neoforged.net/"><img src="https://github.com/user-attachments/assets/dedf4b64-3ab1-444d-9b75-fa1b21640ab7" align="right" height="77"></a>

Como o Minecraft Java Edition não possui código-fonte aberto (Open-source) para podermos editar o conteúdo do jogo, foram criados então os famosos **ModLoaders** que funcionam como uma espécie de painel de controle para edição do código Java do jogo fazendo com que ele reconheça as edições, então foram criados: o **Forge**, o mais utilizado pela comunidade e o **Fabric** que veio para competir com o Forge. Com o tempo o Forge foi depriciado, e surgiu o **NeoForge** para versões posteriores do jogo.

O processo de criação se baseia na investigação, onde os desenvolvedores implementaram a base do código dos seus Load Models para o Forge e o Fabric, onde você pode baixar e editar via IDE.

Portar mods de Minecraft pra versões modernas (tipo 1.20.x e acima, como esse “26.1.2”, uma versão recente do Forge/NeoForge ou algo do tipo) não é uma “atualização simples”, é mais parecido com reconstruir o mod em cima de um Minecraft novo, porque o jogo muda não só nomes de funções, mas estrutura inteira de sistemas internos, renderização, registro de itens, eventos e até coisas básicas como entidades e networking.

O primeiro ponto que você precisa entender é que quase nunca existe “portabilidade automática real”. Essas ferramentas tipo “AI converter mod” ou coisas que prometem converter jar geralmente só funcionam em casos muito simples ou quebram mods medianos/grandes, porque o problema não é só sintaxe, é arquitetura do Minecraft mudando entre versões. Então o caminho real sempre é: pegar o código-fonte do mod (sem isso, praticamente acabou a conversa) e reescrever ele contra o novo mod loader e nova API.

A segunda camada é o salto de mod loader. Se você está indo de Forge antigo pra NeoForge ou Fabric moderno, você não está só atualizando Minecraft, você está trocando o “sistema de encaixe” do mod. Forge e NeoForge ainda são parecidos em conceito, mas APIs mudam nomes, eventos mudam de lugar, registries mudam fluxo, e muita coisa que antes era “hack” ou helper hoje é obrigatório fazer de outro jeito. Já no Fabric, a filosofia é outra, bem mais leve e direta, então port de Forge → Fabric geralmente é praticamente rewrite mesmo. É comum você reaprender o mod enquanto porta.

Depois vem a parte mais pesada: mudanças do próprio Minecraft. Entre 1.16 → 1.18 → 1.20+, o jogo mudou profundamente sistemas como registries, data components, rendering pipeline, worldgen e networking. Isso significa que código antigo quebra em cascata. Um bloco simples que antes era “new Block” hoje pode envolver registry deferred, codecs, components e builders diferentes. É por isso que devs experientes às vezes preferem reescrever features do zero em vez de tentar “consertar port”.

Na prática, o fluxo real de port é mais ou menos assim: você abre o projeto original, atualiza dependências (Java version, mappings, loader), sobe a versão do loader primeiro, resolve os erros um por um (registries quebrados primeiro, depois eventos, depois rendering e client-side, depois worldgen), e só no final você testa gameplay. É um processo iterativo: compila, quebra, ajusta, repete. Não existe “terminar de uma vez”.

Um detalhe importante é que ferramentas de suporte existem, mas elas não fazem o trabalho por você. Existem libs e frameworks tipo Porting Lib que ajudam a reduzir atrito entre loaders e versões, basicamente preenchendo lacunas de API e compatibilidade ([CurseForge][1]), mas isso só facilita, não substitui port manual. E mesmo soluções mais agressivas tipo “converter mods automaticamente” ainda são experimentais e falham bastante em mods reais complexos.

Outro ponto que muita gente ignora: às vezes o maior bloqueio nem é técnico, é design antigo. Mods antigos foram escritos assumindo comportamentos do Minecraft que não existem mais. Então você não “corrige”, você recria a lógica pensando no sistema novo.

Se eu resumir a mentalidade certa pra isso sem romantizar: port de mod não é tradução, é migração de sistema inteiro. Se o mod for pequeno (itens, blocos simples, receitas), dá pra portar relativamente rápido. Se for médio (GUI, entidades, sistemas próprios), já vira reengenharia. Se for grande (worldgen complexo, rendering custom pesado), normalmente vira quase um mod novo.

Se você quiser, me diz um mod específico ou tipo de mod (itens simples, tech mod, dimension mod, etc.) que eu te explico como seria o “caminho real de port” nesse caso, porque muda bastante dependendo da categoria.

[1]: https://www.curseforge.com/minecraft/mc-mods/porting-lib?utm_source=chatgpt.com "Porting Lib - Minecraft Mods - CurseForge"

Mas agora, como rodar mods do forge modloader 1.7.10 em versões mais novas do minecraft como 1.21 utilizando o neoforge ou fabric? Uma espécie de integração de modloaders para preservar mods bons e antigos nas novas versões do Minecraft. Como desenvolvo isso em Java?

Para rodar mods do Forge 1.7.10 em versões modernas, você precisaria de uma solução de compatibilidade que é tecnicamente complexa. Aqui estão as abordagens possíveis:

1) Estrutura do projeto: um esqueleto realista de projeto NeoForge moderno pensando no objetivo (pegar mod 1.7.10 e “encaixar” ele num runtime compatível). Mas já te adianto uma coisa importante: esse tipo de estrutura não “converte mod automaticamente”, ela cria um framework de compatibilidade + reimplementação de APIs antigas, ou seja, você vai simular Forge 1.7.10 dentro de NeoForge 26.1.x em partes, e adaptar o resto manualmente.

```txt
legacy-mods-port/
├── build.gradle
├── settings.gradle
├── gradle.properties
│
├── src/main/java/
│   ├── com/yourname/legacyport/
│   │
│   │   ├── core/
│   │   │   ├── LegacyPortMod.java
│   │   │   ├── ModEntryPoint.java
│   │   │   └── SideProxy.java
│   │   │
│   │   ├── compat/
│   │   │   ├── forge1710/
│   │   │   │   ├── FMLCompatibilityLayer.java
│   │   │   │   ├── ForgeEventBusEmulator.java
│   │   │   │   ├── RegistryBackport.java
│   │   │   │   └── GameRegistryShim.java
│   │   │   │
│   │   │   ├── minecraft/
│   │   │   │   ├── BlockCompat.java
│   │   │   │   ├── ItemCompat.java
│   │   │   │   ├── WorldCompat.java
│   │   │   │   └── EntityCompat.java
│   │   │   │
│   │   │   └── runtime/
│   │   │       ├── LegacyModLoaderRuntime.java
│   │   │       ├── ModTranslationLayer.java
│   │   │       └── CompatibilityBootstrap.java
│   │   │
│   │   ├── transformer/
│   │   │   ├── BytecodeTransformer.java
│   │   │   ├── ASMClassVisitor.java
│   │   │   ├── LegacyNameMapper.java
│   │   │   └── MethodRedirector.java
│   │   │
│   │   ├── mixin/
│   │   │   ├── MinecraftClientMixin.java
│   │   │   ├── WorldMixin.java
│   │   │   └── EntityMixin.java
│   │   │
│   │   ├── loader/
│   │   │   ├── LegacyModDiscoverer.java
│   │   │   ├── ModClassInjector.java
│   │   │   └── ClasspathHacker.java
│   │   │
│   │   └── util/
│   │       ├── ReflectionUtils.java
│   │       ├── Logger.java
│   │       └── ObfuscationHelper.java
│
├── src/main/resources/
│   ├── META-INF/
│   │   └── neoforge.mods.toml
│   │
│   ├── legacyport.mixins.json
│   └── pack.mcmeta
│
└── src/test/java/
    └── com/yourname/legacyport/
        ├── CompatibilityTest.java
        └── RegistryMappingTest.java
```

> [!Important]
> Agora o ponto mais importante não é a árvore em si, mas o papel real de cada camada dentro da ideia de “portador de mods 1.7.10”.

1. A pasta `compat/forge1710` basicamente vira uma “Forge fantasma”, onde você recria o que o mod antigo espera existir. Mods 1.7.10 dependem muito de GameRegistry, eventos globais simples, IDs estáticos e lifecycle do FML antigo, então aqui você cria shims que traduzem isso para o sistema moderno de registries e event bus do NeoForge. Isso é o coração do port.

2. Já `compat/minecraft` não tenta imitar Forge, mas sim “alisar diferenças do Minecraft”. Por exemplo, em 1.7.10 você tinha um modelo de bloco muito mais simples; hoje você precisa lidar com estados de bloco, components e sistemas data-driven. Então essas classes funcionam como adaptadores de comportamento, não de API.

3. A pasta `runtime` é o bootstrap real. Aqui você decide como mods antigos vão ser carregados. Em projetos sérios, essa camada faz três coisas: escaneia mods antigos, injeta classes em runtime e intercepta chamadas críticas antes do NeoForge assumir controle total. É a parte mais perigosa do sistema porque mexe com classloading.

4. O `transformer/` é onde você entra no nível mais baixo possível sem virar “hack loader”. Ele serve para remapear nomes antigos (obfuscation mapping de 1.7.10 é brutal), redirecionar métodos quebrados e, em alguns casos, modificar bytecode pra fazer mods antigos compilarem e rodarem sem alteração total. Aqui você usa ASM ou similar.

5. O `mixin/` entra como ferramenta moderna pra substituir hooks antigos de CoreMod. Em vez de alterar bytecode direto o tempo todo, você injeta comportamento em pontos específicos do Minecraft moderno para simular comportamento antigo. Em port real, mixin reduz muito a necessidade de transformer bruto.

6. E `loader/` é basicamente a “infra de compatibilidade de mods antigos”: ele encontra jars 1.7.10, tenta identificar mod main classes, simula FML lifecycle (preInit, init, postInit), e injeta isso no ciclo moderno do NeoForge. Sem isso, mods antigos nem “nascem” dentro do loader novo.

Se você quiser levar isso para um nível mais real ainda, o próximo passo seria eu te mostrar como seria o `LegacyModLoaderRuntime.java` funcionando de verdade (com simulação de FML 1.7.10 rodando dentro do NeoForge), porque ali está 80% da complexidade prática desse tipo de projeto.

1) **Porting Layer / Compatibility Wrapper** (Abordagem Recomendada): É uma camada de software que traduz chamadas de API antigas (Forge 1.7.10) para APIs modernas (NeoForge/Fabric 1.21), permitindo que código antigo funcione em novas versões sem modificação direta.

```java
// Exemplo conceitual de um adaptador
public class LegacyModAdapter {
    private Map<String, CompatHandler> legacyApis = new HashMap<>();
    
    public void init() {
        // Mapear APIs antigas para novas
        legacyApis.put("net.minecraftforge.event", new ForgeEventAdapter());
        legacyApis.put("cpw.mods.fml.common", new FMLAdapter());
    }
    
    public Object invokeLegacyMethod(String className, String methodName, Object... args) {
        // Traduzir chamadas antigas para novas APIs
        return translateCall(className, methodName, args);
    }
}
```

<img width="136" height="150" align="right" src="https://github.com/user-attachments/assets/d483b856-c0dd-408f-9afd-2d24324ff0fc" />
