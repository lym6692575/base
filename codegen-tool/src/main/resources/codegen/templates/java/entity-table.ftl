package ${package};

<#list imports as impt>
import ${impt};
</#list>
<#if entityExtends?has_content>
@EqualsAndHashCode(callSuper = true)
</#if>
@Entity
@Table(name = "${tableName}")
@Data
public class ${entityName}Entity<#if entityExtends?has_content> extends ${entityExtends}</#if> {

<#list fields as field>
    @ApiModelProperty(value = "${field.label!''}")
    <#if field.id!false>
    @Id
    </#if>
    <#if field.column?has_content>
    @Column(name = "${field.column}")
    </#if>
    private ${field.type} ${field.name};

</#list>
}
