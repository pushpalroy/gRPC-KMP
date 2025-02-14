package io.github.timortel.kotlin_multiplatform_grpc_plugin.generate_mulitplatform_sources.generators

import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.MemberName
import com.squareup.kotlinpoet.MemberName.Companion.member
import io.github.timortel.kotlin_multiplatform_grpc_plugin.generate_mulitplatform_sources.content.*

object Const {
    object Service {

        fun getName(service: ProtoService): String = "KM${service.serviceName.capitalize()}Stub"

        object Constructor {
            const val CHANNEL_PARAMETER_NAME = "channel"
        }

        object JVM {
            const val PROPERTY_JVM_IMPL = "impl"

            fun nativeServiceClassName(protoFile: ProtoFile, service: ProtoService): ClassName =
                ClassName(
                    protoFile.pkg,
                    service.serviceName.capitalize() + "GrpcKt",
                    service.serviceName.capitalize() + "CoroutineStub"
                )
        }

        object JS {
            /**
             * Service name of the js-kotlin bridge class
             */
            fun jsServiceName(service: ProtoService): String = "JS_" + service.serviceName.capitalize()

            fun nativeServiceClassName(protoFile: ProtoFile, service: ProtoService) =
                ClassName(protoFile.pkg, jsServiceName(service))
        }

        object IOS {
            const val CHANNEL_PROPERTY_NAME = "channel"
        }

        object RpcCall {
            const val PARAM_REQUEST = "request"
            const val PARAM_METADATA = "metadata"
        }
    }

    object Message {
        object CommonFunction {
            const val NAME = "common"
            const val PARAMETER_NATIVE = "native"

            object JVM {
                fun commonFunction(attr: ProtoMessageAttribute): MemberName = commonFunction(attr.types.jvmType)

                fun commonFunction(jvmType: ClassName) = MemberName(jvmType.packageName, NAME)
            }

            object JS {
                fun commonFunction(attr: ProtoMessageAttribute): MemberName = commonFunction(attr.types.jsType)

                fun commonFunction(jsType: ClassName) = MemberName(jsType.packageName, NAME)
            }
        }

        object OneOf {
            fun parentSealedClassName(message: ProtoMessage, oneOf: ProtoOneOf) =
                message.commonType.nestedClass(oneOf.capitalizedName)

            fun childClassName(message: ProtoMessage, oneOf: ProtoOneOf, attr: ProtoMessageAttribute) =
                parentSealedClassName(message, oneOf).nestedClass(attr.capitalizedName)

            fun unknownClassName(message: ProtoMessage, oneOf: ProtoOneOf) = parentSealedClassName(message, oneOf).nestedClass("Unknown")
            fun notSetClassName(message: ProtoMessage, oneOf: ProtoOneOf) = parentSealedClassName(message, oneOf).nestedClass("NotSet")

            fun propertyName(message: ProtoMessage, oneOf: ProtoOneOf) = oneOf.name

            object JS {
                fun getCaseFunctionName(oneOf: ProtoOneOf): String = "get${oneOf.name.lowercase().capitalize()}Case"
            }

            object IOS {
                const val REQUIRED_SIZE_PROPERTY_NAME = "requiredSize"

                const val SERIALIZE_FUNCTION_NAME = "serialize"
                const val SERIALIZE_FUNCTION_STREAM_PARAM_NAME = "stream"
            }
        }

        object Attribute {
            /**
             * @return the property name of the given attribute in the generated kotlin file for the message.
             */
            fun propertyName(protoMessage: ProtoMessage, attr: ProtoMessageAttribute): String {
                return when (attr.attributeType) {
                    is io.github.timortel.kotlin_multiplatform_grpc_plugin.generate_mulitplatform_sources.content.Scalar -> attr.name
                    is io.github.timortel.kotlin_multiplatform_grpc_plugin.generate_mulitplatform_sources.content.Repeated -> Repeated.listPropertyName(
                        attr
                    )

                    is MapType -> Map.propertyName(attr)
                }
            }

            object Scalar {
                object JVM {
                    fun getFunction(message: ProtoMessage, attr: ProtoMessageAttribute) =
                        message.jvmType.member(attr.name)

                    fun setFunction(message: ProtoMessage, attr: ProtoMessageAttribute) =
                        message.jvmType.member("set${attr.capitalizedName}")

                    fun setEnumValueFunction(message: ProtoMessage, attr: ProtoMessageAttribute) =
                        message.jvmType.member("set${attr.capitalizedName}Value")

                    fun getHasFunction(message: ProtoMessage, attr: ProtoMessageAttribute) =
                        message.jvmType.member("has${attr.capitalizedName}")
                }

                object JS {
                    fun getFunction(message: ProtoMessage, attr: ProtoMessageAttribute) =
                        message.jsType.member("get${attr.name.lowercase().capitalize()}")

                    fun getHasFunction(message: ProtoMessage, attr: ProtoMessageAttribute) =
                        message.jsType.member("has${attr.name.lowercase().capitalize()}")

                    fun setFunction(message: ProtoMessage, attr: ProtoMessageAttribute) =
                        message.jsType.member("set${attr.name.lowercase().capitalize()}")
                }

                object IOS {
                    fun isMessageSetFunctionName(message: ProtoMessage, attr: ProtoMessageAttribute): String {
                        var name = "is${attr.capitalizedName}Set"
                        val attrNames = message.attributes.map { it.name }

                        while (name in attrNames) {
                            name = "_$name"
                        }

                        return name
                    }
                }
            }

            object Repeated {

                fun listPropertyName(attr: ProtoMessageAttribute) = "${attr.name}List"

                fun countPropertyName(attr: ProtoMessageAttribute) = "${attr.name}Count"

                object JS {
                    fun setListFunctionName(attr: ProtoMessageAttribute): String =
                        "set${attr.name.lowercase().capitalize()}List"

                    fun getListFunctionName(attr: ProtoMessageAttribute): String =
                        "get${attr.name.lowercase().capitalize()}List"

                    fun clearListFunctionName(attr: ProtoMessageAttribute): String =
                        "clear${attr.name.lowercase().capitalize()}List"
                }

                object JVM {
                    fun getListFunction(message: ProtoMessage, attr: ProtoMessageAttribute) =
                        message.jvmType.member("${attr.name}List")

                    fun addAllFunction(message: ProtoMessage, attr: ProtoMessageAttribute) =
                        message.jvmType.member("addAll${attr.capitalizedName}")

                    fun addAllValuesFunction(message: ProtoMessage, attr: ProtoMessageAttribute) =
                        message.jvmType.member("addAll${attr.capitalizedName}Value")
                }
            }

            object Enum {

                object JVM {
                    fun getValueFunction(message: ProtoMessage, attr: ProtoMessageAttribute) =
                        message.jvmType.member("${attr.name}Value")

                    fun getValueListFunction(message: ProtoMessage, attr: ProtoMessageAttribute) =
                        message.jvmType.member("${attr.name}ValueList()")
                }

                object JS {
                    fun getValueFunction(message: ProtoMessage, attr: ProtoMessageAttribute) =
                        message.jsType.member("get${attr.name.lowercase().capitalize()}")

                    fun getValueListFunction(message: ProtoMessage, attr: ProtoMessageAttribute) =
                        message.jsType.member("get${attr.name.lowercase().capitalize()}List")
                }
            }

            object Map {
                fun propertyName(attr: ProtoMessageAttribute): String = "${attr.name}Map"

                object JVM {
                    fun propertyName(attr: ProtoMessageAttribute): String = "${attr.name}Map"

                    fun putAllFunctionName(attr: ProtoMessageAttribute): String = "putAll${attr.capitalizedName}"
                }

                object JS {
                    fun getMapFunctionName(attr: ProtoMessageAttribute): String =
                        "get${attr.name.lowercase().capitalize()}Map"
                }
            }
        }

        object Constructor {
            object JVM {
                const val PARAM_IMPL = "impl"
            }

            object JS {
                const val PARAM_IMPL = "jsImpl"
            }
        }

        object BasicFunctions {
            object EqualsFunction {
                const val NAME = "equals"
                const val OTHER_PARAM = "other"
            }

            object HashCodeFunction {
                const val NAME = "hashCode"
            }
        }

        object IOS {
            object SerializeFunction {
                const val NAME = "serialize"
                const val STREAM_PARAM = "stream"
            }
        }

        object Companion {
            object IOS {
                object DataDeserializationFunction {
                    const val NAME = "deserialize"
                    const val DATA_PARAM = "data"
                }

                object WrapperDeserializationFunction {
                    const val NAME = "deserialize"
                    const val WRAPPER_PARAM = "wrapper"
                }
            }
        }
    }

    object DSL {
        const val buildFunctionName: String = "build"

        object Attribute {
            object Scalar {
                fun propertyName(attr: ProtoMessageAttribute): String = attr.name
            }

            object Repeated {
                fun propertyName(attr: ProtoMessageAttribute): String = "${attr.name}List"
            }

            object Map {
                fun propertyName(attr: ProtoMessageAttribute): String = "${attr.name}Map"
            }
        }

        object OneOf {
            fun propertyName(message: ProtoMessage, oneOf: ProtoOneOf): String = oneOf.name
        }
    }

    object Enum {
        const val getEnumForNumFunctionName = "getEnumForNumber"
        const val VALUE_PROPERTY_NAME = "value"

        fun commonEnumName(protoEnum: ProtoEnum): String = "KM${protoEnum.name.capitalize()}"
        fun commonEnumName(protoEnumName: String): String = "KM${protoEnumName.capitalize()}"

        fun getEnumForNumFunction(protoEnum: ProtoEnum, pkg: String) =
            ClassName(pkg, commonEnumName(protoEnum)).member(getEnumForNumFunctionName)
    }
}