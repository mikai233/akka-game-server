package com.mikai233.tools.excel

import com.mikai233.common.extension.classDependenciesOf
import com.mikai233.common.serde.PrimitiveTypes
import com.mikai233.shared.excel.GameConfig
import com.mikai233.shared.excel.GameConfigManager
import com.mikai233.shared.excel.GameConfigs
import com.squareup.kotlinpoet.*
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import org.reflections.Reflections
import kotlin.io.path.Path
import kotlin.reflect.KClass

fun main() {
    val configImplType = Array::class.asClassName().parameterizedBy(
        KClass::class.asClassName().parameterizedBy(
            WildcardTypeName.producerOf(
                GameConfig::class.asClassName().parameterizedBy(
                    STAR
                )
            )
        )
    )
    val configsImplType = Array::class.asClassName().parameterizedBy(
        KClass::class.asClassName().parameterizedBy(
            WildcardTypeName.producerOf(
                GameConfigs::class.asClassName().parameterizedBy(
                    STAR, STAR
                )
            )
        )
    )
    val configDepsType = Array::class.asClassName().parameterizedBy(
        KClass::class.asClassName().parameterizedBy(
            STAR
        )
    )
    val gameConfigImpls =
        Reflections("com.mikai233.shared.config").getSubTypesOf(GameConfig::class.java).map { it.kotlin }
    val gameConfigsImpls =
        Reflections("com.mikai233.shared.config").getSubTypesOf(GameConfigs::class.java).map { it.kotlin }
    val gameConfigDeps = gameConfigImpls.flatMap { classDependenciesOf(it) }.filter { it.isAbstract.not() }.toSet()
        .filter { it !in PrimitiveTypes }
    val configImplFile = FileSpec.builder("com.mikai233.shared.excel", "ConfigImpl")
        .addProperty(
            PropertySpec.builder("ConfigImpl", configImplType)
                .initializer(buildCodeBlock {
                    add("arrayOf(\n")
                    gameConfigImpls.forEachIndexed { index, gameConfigImpl ->
                        add("%T::class", gameConfigImpl)
                        if (index != gameConfigImpls.size - 1) {
                            add(",\n")
                        }
                    }
                    add("\n)")
                })
                .addKdoc("Generated by GameConfigImplDepsGenerator\n")
                .addKdoc("所有[GameConfig]的实现类\n使用静态注册而不使用运行时反射的原因是为了加快启动速度")
                .build()
        )
        .addProperty(
            PropertySpec.builder("ConfigsImpl", configsImplType)
                .initializer(buildCodeBlock {
                    add("arrayOf(\n")
                    gameConfigsImpls.forEachIndexed { index, gameConfigImpl ->
                        add("%T::class", gameConfigImpl)
                        if (index != gameConfigsImpls.size - 1) {
                            add(",\n")
                        }
                    }
                    add("\n)")
                })
                .addKdoc("Generated by GameConfigImplDepsGenerator\n")
                .addKdoc("所有[GameConfigs]的实现类\n使用静态注册而不使用运行时反射的原因是为了加快启动速度")
                .build()
        )
        .build()
    configImplFile.writeTo(Path("shared/src/main/kotlin"))
    val configDepsFile = FileSpec.builder("com.mikai233.shared.excel", "ConfigDeps")
        .addProperty(
            PropertySpec.builder("ConfigDeps", configDepsType)
                .initializer(buildCodeBlock {
                    add("arrayOf(\n")
                    add("%T::class,\n", GameConfigManager::class)
                    gameConfigDeps.forEachIndexed { index, gameConfigImpl ->
                        add("%T::class", gameConfigImpl)
                        if (index != gameConfigDeps.size - 1) {
                            add(",\n")
                        }
                    }
                    add("\n)")
                })
                .addKdoc("Generated by GameConfigImplDepsGenerator\n")
                .addKdoc("所有[GameConfig]的实现类的依赖\n使用静态注册而不使用运行时反射的原因是为了加快启动速度")
                .build()
        )
        .build()
    configDepsFile.writeTo(Path("shared/src/main/kotlin"))
}