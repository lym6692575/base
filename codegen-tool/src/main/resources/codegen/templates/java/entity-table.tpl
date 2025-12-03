package {{package}};

{{imports}}

@Entity
@javax.persistence.Table(name = "{{tableName}}")
@lombok.Data
public class {{entityName}}Entity{{entityExtends}} {
{{fields}}}