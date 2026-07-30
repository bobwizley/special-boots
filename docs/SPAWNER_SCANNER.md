# Scanner de spawners

Com o mundo fechado, execute a ferramenta informando o caminho da raiz do save:

```bash
./gradlew --quiet scanSpawners --args="/caminho/para/o/save"
```

A ferramenta examina somente os chunks salvos do Overworld e escreve o relatório JSON compacto em stdout. Diagnósticos são escritos em stderr. O processo Java retorna `0` para uma varredura completa, `1` para uma precondição inválida sem relatório e `2` para um relatório parcial.
