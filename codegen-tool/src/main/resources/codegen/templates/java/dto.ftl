package ${package};

<#list imports as import>
import ${import};
</#list>

@lombok.Data
@lombok.EqualsAndHashCode(callSuper=false)
public class ${entityName}Dto${dtoExtends!} {

<#list fields as field>
    @io.swagger.annotations.ApiModelProperty(value = "${field.label}")
    private ${field.type} ${field.name};

</#list>
}
