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

**Cheaper recipe**:
Família de features que substituem uma receita vanilla por uma variante mais barata (ex.: clock, compass).
