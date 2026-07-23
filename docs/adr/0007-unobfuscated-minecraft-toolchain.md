---
number: 007
title: Toolchain para Minecraft 26.2 desofuscado (sem mappings)
date: 2026-07-23
author: Bob Wizley
status: accepted
---

## Context

A partir do fim de 2025 a Mojang parou de ofuscar o Java Edition. O manifest de versão do Minecraft 26.2 não publica mais `client_mappings`/`server_mappings` (só `client` e `server`), e o próprio JAR já vem com os nomes oficiais (`net.minecraft.*`) embutidos. Como consequência, `loom.officialMojangMappings()` falha com `Failed to find official mojang mappings for 26.2`, e Yarn/Quilt não têm builds para 26.x. Isso torna o mecanismo assumido pela ADR-0004 (baixar o arquivo de mappings) inaplicável ao alvo do projeto.

Além disso, o 26.2 exige **Java 25** (o JAR declara `java_version: 25`), e o Fabric Loom só resolve como plugin em versões de Gradle compatíveis.

## Decision

Adotar o toolchain do Fabric para Minecraft desofuscado:

- **Plugin:** `net.fabricmc.fabric-loom` (implementação `LoomNoRemapGradlePlugin`), **não** `net.fabricmc.fabric-loom-remap`. O plugin no-remap trabalha direto com o JAR já nomeado.
- **Sem camada de mappings:** a declaração `mappings loom.officialMojangMappings()` é omitida. O JAR desofuscado já usa os nomes oficiais da Mojang.
- **Dependências sem remap:** como não há remapeamento, as dependências de mod usam `implementation`/`api` comuns, e não as configurações `modImplementation`/`modApi` (que só existem no plugin de remap).
- **Versões fixadas:** Loom `1.17.17`, Gradle `9.6.1` (o Loom 1.17.17 exige Gradle ≥ 9.5.0), Java `25`, Fabric Loader `0.19.3`, Fabric API `0.155.2+26.2`.

## Rationale

- Os nomes usados no código continuam sendo os **nomes oficiais da Mojang** — o objetivo da ADR-0004 (código legível com a nomenclatura oficial) é preservado; apenas o mecanismo mudou, pois não existe mais um arquivo de mappings a baixar.
- O `refs/Wool-Tweaks`, que também tem como alvo 26.2, usa o mesmo plugin `net.fabricmc.fabric-loom` e Java 25, confirmando o caminho.
- Fixar versões (em vez de snapshots) mantém o build reprodutível.

## Consequences

- Esta ADR **refina a ADR-0004**: a intenção (nomenclatura oficial da Mojang) permanece; a implementação (`officialMojangMappings()` + arquivo de mappings) é substituída por "JAR desofuscado, sem mappings".
- O build requer um JDK 25 disponível para o Gradle. A versão-alvo de Java está documentada em `gradle.properties` (`org.gradle.jvmargs`) e em `build.gradle` (`release = 25`).
- Trocar de volta para um alvo ofuscado (versões < 26.1) exigiria reintroduzir mappings e o plugin de remap — por isso a decisão é registrada.
