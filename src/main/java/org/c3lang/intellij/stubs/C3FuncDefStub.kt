package org.c3lang.intellij.stubs

import com.intellij.psi.stubs.*
import org.c3lang.intellij.psi.*
import org.c3lang.intellij.psi.ParamType.Companion.toParamTypeList
import org.c3lang.intellij.psi.ShortType.Companion.toShortType

class C3FuncDefStub(
    parent: StubElement<*>?,
    elementType: IStubElementType<*, *>?,
    val sourceFileName: String,
    val module: ModuleName?,
    val type: ShortType?,
    val fqName: FullyQualifiedName,
    val returnType: ShortType?,
    val parameterTypes: List<ParamType>,
) : StubBase<C3FuncDef?>(parent, elementType) {

    constructor(
        parent: StubElement<*>?,
        elementType: IStubElementType<*, *>?,
        psi: C3FuncDef,
        module: ModuleName? = ModuleName.from(psi),
    ) : this(
        parent = parent,
        elementType = elementType,
        sourceFileName = psi.containingFile.name,
        module = module,
        type = psi.funcHeader.funcName.type?.toShortType(),
        fqName = FullyQualifiedName.from(psi.funcHeader, module),
        returnType = psi.funcHeader.optionalType.type.toShortType(),
        parameterTypes = psi.fnParameterList.parameterList?.paramDeclList.toParamTypeList()
    )

    constructor(
        parent: StubElement<*>?,
        elementType: IStubElementType<*, *>?,
        dataStream: StubInputStream
    ) : this(
        parent = parent,
        elementType = elementType,
        sourceFileName = dataStream.readUTFFast(),
        module = dataStream.readNullableUTFFast()?.let(::ModuleName),
        type = dataStream.readNullableUTFFast()?.let(ShortType::parse),
        fqName = FullyQualifiedName.parse(dataStream.readUTFFast()),
        returnType = dataStream.readNullableUTFFast()?.let(ShortType::parse),
        parameterTypes = ParamType.deserialize(dataStream)
    )

    fun serialize(dataStream: StubOutputStream) {
        dataStream.writeUTFFast(sourceFileName)
        dataStream.writeNullableUTFFast(module?.value)
        dataStream.writeNullableUTFFast(type?.fullName)
        dataStream.writeUTFFast(fqName.fullName)
        dataStream.writeNullableUTFFast(returnType?.fullName)
        ParamType.serialize(dataStream, parameterTypes)
    }

}
