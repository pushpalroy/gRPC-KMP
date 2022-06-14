package io.github.timortel.kotlin_multiplatform_grpc_plugin.generate_mulitplatform_sources.generators.map

import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.CodeBlock
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.PropertySpec
import io.github.timortel.kotlin_multiplatform_grpc_plugin.generate_mulitplatform_sources.content.MapType
import io.github.timortel.kotlin_multiplatform_grpc_plugin.generate_mulitplatform_sources.content.ProtoMessage
import io.github.timortel.kotlin_multiplatform_grpc_plugin.generate_mulitplatform_sources.content.ProtoMessageAttribute
import io.github.timortel.kotlin_multiplatform_grpc_plugin.generate_mulitplatform_sources.generators.Const
import io.github.timortel.kotlin_multiplatform_grpc_plugin.generate_mulitplatform_sources.generators.map.mapper.JsToCommonMapMapper

object JsMapMessageMethodGenerator : MapMessageMethodGenerator(true) {

    override val modifiers: List<KModifier> = listOf(KModifier.ACTUAL)

    override fun modifyMapProperty(
        builder: PropertySpec.Builder,
        protoMessage: ProtoMessage,
        messageAttribute: ProtoMessageAttribute
    ) {
        val mapType = messageAttribute.attributeType as MapType

        val mapVariable = CodeBlock.of(
            "%T<%T, %T>(%N.%N(false))",
            ClassName("io.github.timortel.kotlin_multiplatform_grpc_lib", "JSPBMap"),
            mapType.keyTypes.jsType,
            mapType.valueTypes.jsType,
            Const.Message.Constructor.JS.PARAM_IMPL,
            Const.Message.Attribute.Map.JS.getMapFunctionName(messageAttribute)
        )

        if (mapType.valueTypes.doDiffer || mapType.keyTypes.doDiffer) {
            val initializer = CodeBlock.builder()
                .add("lazy·{\n")
                .add(JsToCommonMapMapper.mapMap("newMap", mapVariable, protoMessage, messageAttribute, mapType))
                .add("\n}\n")

            builder.delegate(initializer.build())
        } else {
            builder.initializer(mapVariable)
        }
    }
}