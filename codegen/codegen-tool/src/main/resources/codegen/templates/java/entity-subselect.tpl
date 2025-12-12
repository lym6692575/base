package {{package}};

{{imports}}

@Entity
@Immutable
@Subselect("{{subselect}}")
@lombok.Data
public class {{entityName}}Entity{{entityExtends}} {
{{fields}}}