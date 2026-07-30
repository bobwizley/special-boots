# RootBoot

Mod único para Minecraft 26.2 (Fabric) que reúne e reimplementa em Java as funcionalidades de vários mods/datapacks de referência.

- **Nome de exibição:** RootBoot
- **Mod id / namespace:** `rootboot`
- **Package base:** `br.com.bobwizley.rootboot`
- **License:** MIT (`LICENSE` na raiz, © 2026 bob@wizley.com.br). Nota: refs originais são CC BY-NC-SA / custom — a licença MIT se apoia na estratégia de reimplementar, não copiar.

## Language

**Feature**:
Unidade de comportamento do mod, reimplementada a partir de uma referência e ligável/desligável de forma independente via config. É a unidade que o feature registry conhece.
_Avoid_: mod, módulo, plugin

**Referência (ref)**:
Um dos projetos originais autônomos em `refs/` cujo comportamento é reimplementado. Serve como especificação, não como fonte para cópia de código.
_Avoid_: mod-fonte, original

**Feature registry**:
Ponto central onde cada feature se registra com um id estável e sua flag de config.

**Toggle de comportamento**:
Flag de configuração, habilitada por padrão, que determina na inicialização se uma feature de comportamento registra seus handlers. Alterações exigem reiniciar o jogo ou servidor; em multiplayer, a configuração de gameplay do servidor é autoritativa.
_Avoid_: toggle em tempo real, hot reload

**Cheaper recipe**:
Família de features que substituem uma receita vanilla por uma variante mais barata (ex.: clock, compass).

**Soul**:
Recipiente recuperável e imóvel, pertencente ao jogador morto, que representa as perdas efetivas de uma única morte. É visível a todos, mas somente o proprietário fora do modo espectador pode ativá-la com clique direito/ação de usar; clique esquerdo, colisão ou mera proximidade não recuperam seu conteúdo. Jogadores terceiros e dano ambiental não podem destruí-la ou deslocá-la, embora operadores possam removê-la por comandos. Um jogador pode possuir várias Souls independentes e persistentes; uma nova Soul não substitui nem funde as anteriores. Ao recuperá-la, os itens retornam ao inventário e apenas o excedente é liberado no local; 80% dos pontos totais exatos de experiência armazenados são creditados diretamente ao proprietário, com arredondamento para baixo. Quando formada no void, permanece na dimensão e nas coordenadas X/Z da morte, sobre o bloco sólido mais alto da coluna ou em Y=96 quando a coluna está vazia. A Soul não é criada quando `keepInventory` impede perdas e, diferentemente da referência, RootBoot não ativa nem usa essa gamerule para formá-la. Desabilitar Soul Links impede novas Souls, mas não desativa nem remove as existentes.

**Duração da Soul**:
Configuração global com dois modos explícitos: permanência ilimitada, que é o padrão, ou prazo em minutos inteiros a partir de 1. O valor zero é inválido e não representa expiração imediata nem eternidade.
_Avoid_: expiração obrigatória, zero sentinela

**Expiração da Soul**:
Quando uma Soul com prazo configurado expira, libera todos os itens no local e materializa em orbs vanilla os mesmos 80% dos pontos totais exatos de experiência, com arredondamento para baixo, que o dono receberia ao recuperá-la. Os itens liberados seguem o estado de Death items don't despawn. Se o chunk estiver descarregado, a Soul é marcada como expirada imediatamente no estado persistente e os drops são materializados uma única vez quando o chunk voltar a carregar, sem carregamento forçado.
_Avoid_: apagar conteúdo, creditar jogador ausente

**Remoção administrativa da Soul**:
Remover por comando a entidade visual de uma Soul finaliza também seu estado persistente e segue o fluxo de expiração: libera itens e 80% da experiência no local ou deixa a materialização pendente até o chunk carregar. A remoção não pode apagar silenciosamente nem deixar órfão o conteúdo.
_Avoid_: remoção visual isolada, descarte administrativo

**Relógio da Soul**:
O prazo de uma Soul avança enquanto seu proprietário está online, independentemente de a Soul ou seu chunk estarem carregados. O relógio pausa enquanto o proprietário está offline.
_Avoid_: tempo de chunk, tempo offline

**Política global de duração das Souls**:
A duração configurada é aplicada dinamicamente a todas as Souls existentes. Alterar o número de minutos ou selecionar permanência ilimitada atualiza a política de todas elas, não apenas das criadas depois da mudança. Ao reiniciar com um prazo reduzido, Souls cujo tempo acumulado já alcançou o novo limite expiram imediatamente e liberam seus itens e experiência. No modo de permanência ilimitada, o tempo já acumulado permanece salvo e congelado; se um prazo voltar a ser configurado, a contagem retoma desse valor.
_Avoid_: duração capturada na criação

**Item de morte protegido**:
Drop de uma morte de jogador impedido apenas de desaparecer pelo tempo enquanto a feature correspondente está habilitada. Continua vulnerável a lava, fogo, explosões, cactos, vazio e demais perigos conforme as regras vanilla. Inclui o conteúdo excedente liberado por uma Soul recuperada enquanto a proteção está ativa. Ao desabilitá-la, itens já protegidos voltam ao ciclo vanilla com um novo prazo completo de despawn.
_Avoid_: item permanente

**Resgate do void**:
Ativação de um totem carregado pelo jogador ao alcançar o void, seguida de até 60 segundos de movimento vertical controlado: sobe normalmente e desce lentamente ao agachar. Termina ao pousar ou ao esgotar o tempo.
_Avoid_: voo, modo criativo

**Colheita elegível**:
Quebra de uma plantação completamente madura por um jogador sem Silk Touch. Plantas imaturas ou colheitas com Silk Touch não podem conceder a recompensa de Crops XP; culturas comuns têm 75% de chance e melão ou abóbora têm 100% de gerar um orb de 1 ponto.
_Avoid_: quebra de plantação, cultivo parcial

**Orb teleguiado**:
Orb de experiência que, após 20 ticks, persegue dinamicamente e através de blocos o jogador não-espectador mais próximo em até 64 blocos. Move-se a 0,3 bloco por tick nos primeiros 10 ticks de perseguição e a 0,6 depois. Sem alvo válido nesse alcance, permanece imóvel; a regra é uniforme em todas as dimensões e locais.
_Avoid_: XP do jogador, orb com proprietário

**Vínculo de silêncio**:
Associação persistente entre um mob elegível e um echo shard consumido para silenciá-lo e impedir seu despawn natural. Água desfaz o vínculo e solta o shard; a morte do mob sempre inclui um shard entre os drops no local, independentemente da causa. Desabilitar a feature impede novos vínculos, mas não altera nem interrompe os existentes.
_Avoid_: efeito de silêncio, mute temporário

**Disputa por echo shard**:
Quando um único shard está a até 1 bloco de vários mobs elegíveis, vincula-se somente ao mais próximo. Empates exatos são resolvidos por uma ordem estável baseada no UUID da entidade; um shard nunca silencia mais de um mob.
_Avoid_: vínculo múltiplo, escolha dependente da ordem do tick

**Display oculto**:
Armor stand, item frame ou glow item frame cuja invisibilidade persistente foi aplicada pelo RootBoot por meio de poção splash. Água splash reverte somente essa invisibilidade, sem alterar estados invisíveis originados por comandos ou outros mods. Desabilitar a feature impede novas aplicações, mas não revela displays existentes nem bloqueia sua reversão com água.
_Avoid_: entidade invisível

**Cabeça memorial**:
Cabeça do jogador solta separadamente no local de toda morte para marcá-lo. Não integra a Soul nem é suprimida por `keepInventory`; enquanto item, não sofre dano nem despawn. No void, permanece na dimensão e X/Z da morte, sobre o bloco sólido mais alto da coluna ou em Y=96 se a coluna estiver vazia.
_Avoid_: drop de inventário, conteúdo da Soul

**Anúncio do dia**:
Exibição traduzível do número do dia global do Overworld na action bar no início de cada dia e em toda entrada ou reconexão ao mundo. Alcança jogadores em qualquer dimensão; dimensões não possuem contagens independentes. O anúncio comum permanece plenamente visível por cerca de 2 segundos; a transição para um múltiplo de 100 usa uma animação especial de aproximadamente 11 segundos, com cores e sons, que não se repete em reconexões durante o mesmo dia.
_Avoid_: mensagem de primeiro acesso

**Alerta de vida baixa**:
Batida ouvida somente pelo jogador quando um dano não fatal o deixa com até 8 pontos de vida, equivalente a 4 corações. Ocorre uma vez por evento de dano, sem cooldown adicional.
_Avoid_: som ambiente, alerta global

**Anúncio de morte**:
Mensagem traduzível publicada para todos os jogadores online quando alguém morre, contendo o nome, o tempo sobrevivido e a contagem total de mortes vanilla daquele jogador, inclusive o histórico anterior ao RootBoot. O tempo sobrevivido avança somente enquanto o jogador está conectado e vivo, pausa offline, continua na reconexão e zera após cada morte; antes da primeira morte, começa na primeira entrada.
_Avoid_: estatística privada

**Escada pendurada**:
Escada colocada abaixo da extremidade de uma coluna existente por um novo pressionamento de agachar enquanto o jogador está na coluna e segura uma escada. Coloca uma unidade por pressionamento, consome o item fora do criativo e depende da cadeia acima para permanecer sustentada. Se houver escadas nas duas mãos, consome primeiro da mão principal e usa a secundária somente como fallback.
_Avoid_: extensão contínua, escada gratuita

**Mob bebê**:
Qualquer entidade viva que o Minecraft reconheça como variante bebê, independentemente de ser passiva, neutra ou hostil. Enquanto a feature está ativa, possui metade da vida máxima efetiva que teria no ambiente atual sem Half-health Babies, incluindo modificadores fornecidos por outros mods; a redução é multiplicativa, não a substituição por metade de um valor vanilla fixo. A vida atual mantém a mesma proporção ao entrar ou sair desse estado. Ao desabilitar a feature, recupera a vida máxima sem a redução do RootBoot quando carregado, preservando a proporção atual.
_Avoid_: baby zombie, filhote hostil

**Piglin com trim**:
Piglin comum cuja peça de armadura já equipada recebeu um trim no nascimento. Cada peça rola independentemente com chance de 25% fora de bastions e 50% dentro deles; piglin brutes não são elegíveis. Peças selecionadas escolhem uniformemente entre Rib com diamond/iron/netherite/gold ou Snout com diamond/netherite/gold; leggings também incluem Silence com gold, com o mesmo peso. A rolagem ocorre uma única vez no spawn; habilitar a feature não processa nem rerrola piglins existentes.
_Avoid_: piglin sorteado, piglin brute com trim

**Descoberta de bioma**:
Primeira entrada de um jogador em um ID de bioma registrado. O bioma atual também é verificado no login ou reconexão e anunciado se ainda não estiver registrado. É persistida individualmente e anunciada uma única vez com o nome traduzido do bioma ou, na ausência dele, com seu ID formatado; inclui biomas vanilla e modded.
_Avoid_: categoria de bioma, descoberta global

**Descoberta do End**:
Primeira entrada de um jogador no End em determinado mundo. Login ou reconexão já dentro do End também conta como entrada elegível se a descoberta ainda não estiver registrada. Dispara uma única vez um título dramático e permanece registrada para não se repetir em entradas posteriores.
_Avoid_: transição recorrente de dimensão

**Descoberta do Wither**:
Primeira aproximação de um jogador a até 64 blocos de qualquer Wither. Login ou reconexão já dentro desse raio também conta como aproximação elegível se a descoberta ainda não estiver registrada. Dispara uma única vez um título dramático e permanece registrada, independentemente de quem invocou o Wither.
_Avoid_: anúncio global de invocação

**Descoberta com feature desabilitada**:
Eventos elegíveis de entrada no End ou aproximação de Wither não são registrados enquanto Subtitles on Major Events está desabilitada. Depois de habilitada, o primeiro evento elegível posterior exibe o título e cria o registro persistente.
_Avoid_: descoberta silenciosa

**Descoberta de bioma com feature desabilitada**:
Biomas visitados enquanto Subtitles on Biome Discovery está desabilitada não são registrados. Depois de habilitada, a primeira entrada em cada bioma gera o anúncio e cria o registro persistente, ainda que o jogador já o tenha visitado durante a desativação.
_Avoid_: histórico retroativo de biomas

**Recompensa de Crops XP**:
Rolagem única por colheita elegível que, no sucesso, gera exatamente um orb de 1 ponto de experiência. Fortuna não altera a chance nem a quantidade; Toque Suave torna a colheita inelegível.
_Avoid_: escala com Fortuna, múltiplos orbs

**Vazio resgatável por totem**:
Dano do tipo `out_of_world` em qualquer dimensão vanilla ou modded pode ativar Totem Works In Void. A elegibilidade depende da causa do dano, não de uma lista fixa de dimensões.
_Avoid_: somente End, detecção por dimensão

**Prazo do resgate por totem**:
Cada ativação concede seu próprio prazo completo de até 60 segundos. O término encerra apenas o resgate atual; um novo evento letal de dano `out_of_world` pode consumir outro totem pelo fluxo vanilla e iniciar um novo prazo.
_Avoid_: renovação automática, bloqueio permanente

**Supressão por jukebox**:
Estado local em que a música ambiente permanece impedida de tocar enquanto existe ao menos um jukebox com disco em reprodução dentro do alcance audível. A detecção independe do volume configurado pelo usuário para a categoria sonora do jukebox. Ao deixar de haver reprodução ativa no alcance, o agendador vanilla volta a operar sem retomar a faixa ambiente interrompida.
_Avoid_: interrupção na inserção, pausa de faixa

**Silêncio de morte**:
Interrupção somente da música ambiente local quando o jogador morre. Não interrompe jukeboxes nem outros sons do mundo.
_Avoid_: silêncio global, parar discos

**Som local de morte**:
Som ouvido somente pelo jogador morto e selecionado conforme a causa da morte. Damage types sem mapeamento específico, inclusive os adicionados por outros mods, usam um som de morte genérico local.
_Avoid_: silêncio para causa desconhecida, associação arbitrária

**Limiar de vida baixa**:
Até 8 pontos da vida real do jogador após um evento de dano não fatal. Corações de absorção não aumentam o limiar; um golpe absorvido integralmente, sem redução da vida real, não dispara o som.
_Avoid_: som por perda somente de absorção, somatório com absorção

**Marco de nível**:
Cruzamento ascendente de ao menos um múltiplo de 5 no nível de experiência do jogador. Dispara uma única animação local, visível somente para aquele jogador, por aumento, mesmo quando vários marcos são cruzados; reduções nunca disparam. No login ou na primeira inicialização após habilitar a feature, o nível atual é registrado silenciosamente como baseline.
_Avoid_: todo level up, uma animação por nível

**Raio do Heavyfoot**:
Distância quadrada de atuação ao redor do bloco ocupado pelo jogador: 0 corresponde a 1×1, 1 a 3×3 e 2 a 5×5. O valor padrão é 1.
_Avoid_: raio inerte, diâmetro

**Exclusividade dos encantamentos de botas**:
Heavyfoot e Lightfoot são mutuamente exclusivos: uma mesma bota não pode conter ambos. Esta é uma divergência deliberada da referência `special-boots`, que não declara incompatibilidade entre eles.
_Avoid_: encantamentos combináveis

**Encantamento de bota desabilitado**:
Heavyfoot ou Lightfoot já presente em uma bota permanece intacto quando sua feature é desabilitada, mas seu efeito fica suspenso. Após reabilitar a feature e reiniciar, o mesmo encantamento volta a funcionar.
_Avoid_: remover encantamento, reescrever item

**Tridente leal preservado**:
Tridente com Lealdade impedido de ser destruído ao entrar no vazio. Se o dono estiver morto, desconectado ou em outra dimensão, permanece persistente até que o retorno vanilla possa continuar com o dono na mesma dimensão; não teleporta entre dimensões nem é inserido diretamente no inventário.
_Avoid_: entrega diferida, teleporte de tridente

**Time Offset**:
Transformação única avaliada somente na primeira inicialização do mundo. Se estiver habilitada, acrescenta um dia para que o mundo comece no dia 1; mudanças posteriores no toggle não aplicam nem revertem o deslocamento. A configuração deve informar que a opção afeta apenas mundos criados posteriormente.
_Avoid_: migração retroativa, offset reversível

**Cabeça de jogador protegida**:
Entidade de item original gerada pelo RootBoot na morte, que permanece invulnerável e sem despawn até ser coletada. Desabilitar Player Head Drop impede novos drops, mas não remove a proteção das entidades já existentes. Ao entrar em um inventário, a proteção termina; se a cabeça for descartada novamente, segue dano e despawn vanilla.
_Avoid_: desproteção retroativa, item permanentemente marcado

**Grupo de spawners simultâneos**:
Conjunto máximo de pelo menos dois blocos `minecraft:spawner` para os quais existe um único ponto de ativação comum. Nenhum outro spawner pode ser acrescentado sem eliminar esse ponto, e seus subconjuntos não são resultados separados. Grupos máximos distintos podem compartilhar spawners. A proximidade isolada entre cada par não basta para formar um grupo; `minecraft:trial_spawner` não participa.
_Avoid_: par de spawners, subconjunto redundante, grupo necessariamente disjunto, spawners apenas próximos, trial spawner

**Ponto de ativação comum**:
Posição de coordenadas X, Y e Z inteiras situada a menos de 16 blocos do centro de cada spawner de um grupo, a partir da qual um único jogador pode ativar todos simultaneamente. A comparação reproduz o limite estrito do Minecraft 26.2: estar exatamente a 16 blocos não ativa o spawner. A posição não precisa estar vazia, possuir piso nem ser atualmente acessível. Uma interseção geométrica que contenha apenas pontos fracionários não basta.
_Avoid_: centro do grupo, posição fracionária, posição de um spawner, posição transitável

**Ponto ótimo de ativação**:
Ponto de ativação comum inteiro que minimiza a maior distância até qualquer spawner do grupo. É o ponto apresentado pelo scanner para oferecer a maior margem possível de ativação simultânea e não resulta do arredondamento de um ponto fracionário.
_Avoid_: ponto válido arbitrário, ponto fracionário arredondado, média das posições

**Território examinável pelo scanner de spawners**:
Todos os chunks gerados e salvos do Overworld, independentemente da distância até o spawn do mundo. Chunks ainda não gerados não pertencem ao território examinável.
_Avoid_: chunks carregados, raio do spawn, território inexplorado
