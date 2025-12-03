package {{package}};

{{imports}}

@lombok.Data
@lombok.EqualsAndHashCode(callSuper=false)
public class {{entityName}}Dto{{dtoExtends}} {
{{fields}}}