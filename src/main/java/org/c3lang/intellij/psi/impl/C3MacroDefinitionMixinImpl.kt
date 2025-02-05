package org.c3lang.intellij.psi.impl

import com.intellij.lang.ASTNode
import com.intellij.psi.stubs.IStubElementType
import com.intellij.psi.tree.IElementType
import org.c3lang.intellij.psi.C3MacroDefinition
import org.c3lang.intellij.psi.C3PsiElement
import org.c3lang.intellij.psi.FullyQualifiedName
import org.c3lang.intellij.psi.ModuleName
import org.c3lang.intellij.psi.ParamType
import org.c3lang.intellij.psi.ParamType.Companion.toParamTypeList
import org.c3lang.intellij.psi.ShortType
import org.c3lang.intellij.psi.ShortType.Companion.toShortType
import org.c3lang.intellij.stubs.C3MacroDefinitionStub

abstract class C3MacroDefinitionMixinImpl : C3StubBasedPsiElementBase<C3MacroDefinitionStub>, C3MacroDefinition {

    constructor(node: ASTNode) : super(node)

    constructor(
        stub: C3MacroDefinitionStub,
        nodeType: IStubElementType<*, *>
    ) : super(stub, nodeType)

    constructor(
        stub: C3MacroDefinitionStub,
        nodeType: IElementType?,
        node: ASTNode?
    ) : super(stub, nodeType, node)

    override val nameElement: C3PsiElement?
        get() = macroHeader?.macroName

    override val sourceFileName: String
        get() = containingFile.name

    override val fqName: FullyQualifiedName
        get() = stub?.fqName ?: FullyQualifiedName.from(this)

    override val moduleName: ModuleName?
        get() = stub?.module ?: ModuleName.from(this)

    override val type: ShortType?
        get() = stub?.type ?: macroHeader?.macroName?.type?.toShortType()

    override val returnType: ShortType?
        get() = stub?.returnType ?: macroHeader?.optionalType?.type?.toShortType()

    override val parameterTypes: List<ParamType>
        get() = stub?.parameterTypes ?: macroParams?.parameterList?.paramDeclList.toParamTypeList()
}