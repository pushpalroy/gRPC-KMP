package io.github.timortel.kotlin_multiplatform_grpc_plugin.generate_mulitplatform_sources.generators

import com.squareup.kotlinpoet.*
import io.github.timortel.kotlin_multiplatform_grpc_plugin.generate_mulitplatform_sources.generators.common.JvmCommonFunctionGenerator
import io.github.timortel.kotlin_multiplatform_grpc_plugin.generate_mulitplatform_sources.generators.dsl.JvmDslBuilder
import io.github.timortel.kotlin_multiplatform_grpc_plugin.generate_mulitplatform_sources.generators.proto_file.JvmProtoFileWriter
import io.github.timortel.kotlin_multiplatform_grpc_plugin.generate_mulitplatform_sources.content.ProtoFile
import java.io.File

fun writeJvmFiles(protoFile: ProtoFile, jvmOutputDir: File) {
    JvmProtoFileWriter(protoFile).writeFile(jvmOutputDir)

    //JVM helper
    FileSpec
        .builder(protoFile.pkg, protoFile.fileNameWithoutExtension + "_jvm_helper")
        .apply {
            JvmCommonFunctionGenerator(this).generateCommonGetter(protoFile.messages)
        }
        .build()
        .writeTo(jvmOutputDir)

    writeDSLBuilder(protoFile, JvmDslBuilder, jvmOutputDir)
}