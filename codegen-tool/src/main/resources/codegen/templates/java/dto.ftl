package ${package};

<#list imports as import>
import ${import};
</#list>

@Data
@EqualsAndHashCode(callSuper=false)
public class ${entityName}Dto<#if entityExtends?has_content>${dtoExtends}</#if> {

<#list fields as field>
    @ApiModelProperty(value="${field.label}")
    private ${field.type} ${field.name};

</#list>
}
