package br.org.semeando.comum;

import org.hibernate.dialect.type.PostgreSQLEnumJdbcType;
import org.hibernate.type.descriptor.java.JavaType;
import org.hibernate.type.descriptor.jdbc.JdbcLiteralFormatter;

/**
 * Enum nativo do PostgreSQL cujo tipo foi criado pelo Prisma com aspas ({@code "StatusMatricula"}).
 * O Hibernate escreve literais como {@code 'ATIVA'::StatusMatricula}, sem aspas — e o PostgreSQL
 * procura {@code statusmatricula}, que não existe. Aqui o nome do tipo sai entre aspas.
 * Uso: {@code @JdbcType(EnumPostgres.class)} em todo campo enum.
 */
public class EnumPostgres extends PostgreSQLEnumJdbcType {

    @Override
    public <T> JdbcLiteralFormatter<T> getJdbcLiteralFormatter(JavaType<T> javaType) {
        String tipo = '"' + javaType.getJavaTypeClass().getSimpleName() + '"';
        return (appender, valor, dialect, opcoes) -> {
            appender.appendSql('\'');
            appender.appendSql(valor instanceof Enum<?> e ? e.name() : valor.toString());
            appender.appendSql("'::");
            appender.appendSql(tipo);
        };
    }
}
