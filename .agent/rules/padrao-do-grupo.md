# Padrão do grupo DoaMais

- Stack: Java puro (sem Spring/frameworks), `com.sun.net.httpserver`, SQLite via JDBC, HTML/CSS/JS. Não adicionar outras bibliotecas sem avisar o grupo.
- Pacotes: `com.doamais.{db,model,dao,handler,util}`. Cada integrante só edita os arquivos do seu módulo (ver README).
- Todo SQL deve usar `PreparedStatement` e ficar apenas nos DAOs. Conexões via `Database.getConnection()` em try-with-resources.
- Datas como `LocalDate` no Java e texto ISO no banco. Tipos sanguíneos: `A+ A- B+ B- AB+ AB- O+ O-`.
- Git: nunca commitar/push na `main`. Antes de começar: `git pull origin main`, criar branch `feat/<modulo>`, commits pequenos em português, `git push -u origin <branch>`.
- Build: `build.bat`.
