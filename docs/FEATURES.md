# Features — spec corrente

Registro das decisões de escopo tomadas na sessão de grilling, ref por ref. Alvo: Minecraft 26.2, Fabric. Cada feature é toggleável via config (ver ADR-0002).

Legenda: ✅ entra · ❌ fora · 🔧 entra com alteração · ❔ pendente.

## Princípios globais

- **Não duplicar vanilla 26.2:** receitas/mecânicas dos refs que já se tornaram vanilla na 26.2 **não** são reimplementadas (evita id de receita duplicado e trabalho redundante). Ex.: corte direto de deepslate e corte de cobre já são vanilla.
- **Sem filosofia de preço única:** o mod não força um balanço único. Cada feature preserva a intenção do seu ref (ex.: OmniCut = "preço vanilla"; More-Buttons = generoso). Conflitos de receita são resolvidos por cessão de propriedade, não por unificação de preço.

---

## cheaper-clocks

- 🔧 **Clock mais barato** — substitui a receita vanilla `minecraft:clock`.
  - Vanilla: 4 gold ingots + 1 redstone.
  - **Decisão:** **4 `gold_nugget` + 1 `redstone`**, formato diamante (nuggets nos 4 braços, redstone no centro), resultado 1 clock. Sem `gold_ingot`.
    ```
     #      # = gold_nugget (×4)
    #X#     X = redstone (×1)
     #
    ```

---

## cheaper-compasses

- 🔧 **Compass mais barato** — substitui a receita vanilla `minecraft:compass`. Espelho do clock.
  - Vanilla: 4 iron ingots + 1 redstone.
  - **Decisão:** **4 `iron_nugget` + 1 `redstone`**, formato diamante, resultado 1 compass. Sem `iron_ingot`.

---

## Village-Bell-Recipe

- ✅ **Sino craftável** — receita **aditiva** (vanilla não tem craft de bell). Ref já mira 26.2.
  - **Decisão:** receita como o ref, resultado 1 `minecraft:bell`.
    ```
    LLL     L = #minecraft:logs (×3, via tag)
    SGS     G = minecraft:gold_block (×1)
    S S     S = minecraft:stone (×4)
    ```

---

## More-Buttons

- ✅ **Botões via crafting (override)** — 1 bloco → **4** botões (vanilla = 1).
  - Madeira (×12): acacia, bamboo, birch, cherry, crimson, dark_oak, jungle, mangrove, oak, pale_oak, spruce, warped (1 planks → 4).
  - Pedra (×2): stone, polished_blackstone (1 bloco → 4).
- ✅ **Botões via stonecutting (aditivo)** — 1 bloco → **6** botões.
  - stone → 6 stone_button; polished_blackstone → 6 polished_blackstone_button.
- **Decisão:** entra tudo; crafting = 4/bloco; stonecutter = 6/bloco (versão generosa; o mod **não** adota filosofia "sem bônus" única).
- ✅ **Resolvido — dono dos botões de pedra no stonecutter:** More-Buttons registra `stone_button` e `polished_blackstone_button` via stonecutter (count 6). OmniCut **não** registra essas duas (cedidas aqui).

---

## Recycling

Recuperação de material base a partir de gear. **Output sempre 1 unidade** (recuperação parcial — não dá dupe). Sobrescreve o comportamento vanilla (ferro/ouro dão nugget na vanilla; aqui dão ingot inteiro — buff deliberado).

- ✅ **Reciclagem na fornalha** (smelting + blasting) — 6 materiais:
  - copper → 1 copper_ingot · chainmail → 1 iron_ingot · iron → 1 iron_ingot · golden → 1 gold_ingot · diamond → 1 diamond · netherite → 1 netherite_scrap.
  - Aceita todo o gear do material: helmet/chestplate/leggings/boots + sword/pickaxe/axe/shovel/hoe + horse_armor + nautilus_armor + spear.
- ✅ **Reciclagem na crafting table** — 3 materiais (não vão à fornalha):
  - leather (4 peças de armadura + `leather_horse_armor`) → 1 leather.
  - turtle (`turtle_helmet`) → 1 turtle_scute.
  - armadillo (`wolf_armor`) → 1 armadillo_scute.
- ⚠️ **Verificar na implementação:** existência dos itens `copper_*`, `nautilus_armor`, `spear` no registry da 26.2 (o ref assume que existem).

---

## OmniCut

Woodcutting / stonecutting / recycling. Preço "vanilla" preservado (calibragem pré-calculada do ref). ~409 receitas no ref; na 26.2 reimplementamos só o que **não** é vanilla. Pacote inteiro confirmado.

- ✅ **Wood cutting** (stonecutter) — log/wood → 15 produtos: stairs(×4), slab(×8), fence, fence_gate, door, trapdoor, sign, boat, planks, stick, wood, strip_log, strip_wood, pressure_plate, button(×4). 11 tipos de madeira. Aceita log/stripped_log/wood/stripped_wood como input.
- ✅ **Wood recycling** (stonecutter) — produtos de madeira → planks/sticks.
- ✅ **Copper recycling** (stonecutter uncut + bancada unslab) — sem "cut" (corte de cobre é vanilla).
- 🔧 **Rock cutting** — só o que não é vanilla na 26.2: deepslate direto é vanilla (skip); botões cedidos ao More-Buttons. Sobra essencialmente **cinnabar/sulfur** (ver abaixo).
- ✅ **Stone recycling** (stonecutter uncut) — produtos de pedra → bloco base (inclui smooth/cracked → base).
- ✅ **Slab reassembly** (unslab, bancada) — 2 slabs lado a lado → bloco. Rock + copper + wood.
- ✅ **Pedras novas 26.2** — cinnabar e sulfur: cut/unslab (overlay 26.2 do ref).
- **Notas:** reciclagem é só stonecutter (README menciona bancada, mas os dados não têm). Botões de madeira do OmniCut (stonecutter, log→4) coexistem com os do More-Buttons (bancada, planks→4) — ids/tipos distintos, sem conflito.

---

## better-trees

Melhorias estéticas nas árvores do overworld. Sobrescreve ~40 `configured_feature` vanilla (oak, birch, cherry, mangrove, mega_spruce, variantes `_bees`, etc.). Baseline = conjunto do **overlay 26.1** (inclui pale_oak + variantes `_leaf_litter`).

- ✅ **Árvores melhoradas** — reimplementado via Fabric datagen (Java → JSON que sobrescreve os ids vanilla).
- **Toggle:** ❌ **não toggleável — sempre ligado** quando o mod carrega. **Exceção ao ADR-0002** (worldgen resolve antes de config runtime; por decisão, não expomos como datapack opcional).
- Afeta apenas chunks novos.
- ❌ `cleartrees.mcfunction` (utilitário admin do autor) — **descartado**.

---

## Wool-Tweaks

Reduzido a uma única receita.

- ✅ **`wool → 4 string`** — shapeless, 1 `#minecraft:wool` (qualquer cor, via tag) → 4 string. Aditivo.
- ❌ **Recolorir por clique-direito** (wool/bed/carpet) — **fora**.
- ❌ **Re-tingir na grade** (48 receitas wool/bed/carpet) — **fora**.
- Nota: sem o clique-direito, não há dependência de event handler nem do Collective; é só receita.

---

## special-boots

Dois encantamentos de bota (single-level, slot feet, `supported_items: #minecraft:enchantable/foot_armor`, weight 5, custo 15–45, anvil_cost 2). Config própria absorvida na Cloth unificada.

- ✅ **Heavyfoot** — `EnchantmentEntityEffect` custom em `minecraft:tick`: num raio ao redor do player, converte grass/dirt/coarse_dirt/podzol/mycelium/rooted_dirt → `dirt_path` (abaixo) e destrói flores (`#minecraft:flowers`)/short_grass/tall_grass/fern/large_fern/dead_bush/sweet_berry_bush (pés e cabeça).
  - **Raio default = 1** (ref usava 0/inerte); range **0–2** na config.
- ✅ **Lightfoot** — mixin `@Redirect` em `FarmBlock.fallOn`→`turnToDirt`: impede pisoteio de farmland se a bota tiver o encantamento.
- **Obtenção (opção A):** survival via **mesa de encantar + bigorna** — adicionar às tags `#minecraft:in_enchanting_table` e `non_treasure`. Sem trades, sem loot/treasure.
- ⚠️ **Verificar na implementação:** API de enchantment/`FarmBlock`/`Identifier` na 26.2 (ref é 1.21.11, muito próximo).

---

## Vanilla-Refresh (subconjunto curado)

Só as 18 features abaixo. Reimplementação em Java (os refs são `.mcfunction`; a mecânica exata de cada uma será lida das funções na implementação). `(C)` = client-side, `(S)` = server/gameplay.

**Entram:**
- ✅ #3 **Daycounter** `(C)` — conta dias, exibe no início do dia; animação em marcos de 100.
- ✅ #4 **Homing Experience Orbs** `(S)` — orbs de XP voltam ao player.
- ✅ #6 **Crops XP** `(S)` — chance de XP ao colher plantações.
- ✅ #7 **Jukebox Music Override** `(C)` — música do jukebox sobrepõe a do jogo.
- ✅ #8 **Extra Loyal Tridents** `(S)` — tridente loyalty volta ao cair no void.
- 🔧 #9 **Improved Baby Zombies** `(S)` — **apenas vida pela metade**. Sprint-jump e one-shot (crítico diamante/netherite) **fora**.
- ✅ #10 **Improved/Local Death Sound** `(C)` — som de morte **por-causa, local** (só você ouve). **Sem** sub-opção de "som fixo".
- ✅ #12 **Drop Ladder** `(S)` — sneak na escada estende escadas penduradas.
- ✅ #13 **Totem Works In Void** `(S)` — totem no void → levitação controlável até o chão.
- ✅ #14 **Player Head Drop** `(S)` — dropa a cabeça do player ao morrer.
- ✅ #15 **Echo Shard Silence** `(S)` — echo shard sobre mob o silencia.
- ✅ #16 **Time Offset** `(S)` — mundo começa no dia 1 (shift único de +1 dia na criação). **Só mundos novos** — sem +1 retroativo em mundos existentes.
- 🔧 #19 **Improved Player Animations** `(C)` — **apenas level up** (splash na água **fora**).
- ✅ #21 **Subtitles on Major Events** `(C)` — título ao invocar Wither / entrar no End.
- ✅ #22 **Subtitles on Biome Discovery** `(C)` — nome do bioma ao descobrir.
- ✅ #23 **Trimmed Armored Piglins** `(S)` — ~8% piglins com trim (16% em bastions) — ref defaults.
- ✅ #27 **Low Health Sound** `(C)` — batida de coração ao levar dano com vida baixa.
- ✅ #30 **Stands & Frames Invisibility** `(S)` — splash invis → invisível; splash water → reaparece.

**Do grupo "disabled by default" do VR — entram como ENABLED by default aqui:**
- ✅ A **Death items don't despawn** `(S)` — itens dropados na morte não despawnam (persistência indefinida); reverte ao desligar.
- ✅ B **Soul Links** `(S)` — ao morrer, itens+XP viram uma "Soul"; dono toca → recupera (80% do XP); imune a lava e void (tp p/ terra mais alta ou y96). *(complexo)*
- ✅ D **Stop Music on Death** `(C)` — para a música ao morrer.
- ✅ J **Death Stats** `(S)` — anuncia contagem de mortes + tempo desde a última.
- ⚠️ **Interação A × B:** ambos protegem itens na morte. Default: **Soul Links (B) tem precedência** — com B ligada, os drops vão para a Soul e A não se aplica; A rege quando B está desligada. (Revisar na implementação.)

**Fora:** #1 Player Sitting, #2 Mob Health Display, #5 Craft Sounds, #11 Better Armor Stands, #17 Wither Head Drop, #18 Equipable Banners, #20 Improved Block Animations, #24 Global Death Sound, #25 Spectator Ghost, #26 Griefing Gamerules, #28 Better Lodestones (N/A na 26.2), #29 Path Sprinting, #31 Recovery Coordinates, #32 Readable Clocks, #33 Party Cake, #34 Improved Spectator, #35 Wands, #36 Item Sets, #37 Admin Commands, #38 Join/Exit Sounds, #39 Player Statistics, #40 Viewable Gamerules, #41 Playerlist. Disabled-by-default fora: C First Join Chat, E Enderdragon Drops Elytra, F Renewable Dragon Eggs, G Looping Jukebox (obsoleto/vanilla), H Gravestones, I Tips, K Dynamic Optifine Lighting (buggy), L Exploding Blast Furnace (piada).
