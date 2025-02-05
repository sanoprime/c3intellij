package org.c3lang.intellij.psi.impl

import com.intellij.lang.ASTNode
import com.intellij.psi.stubs.IStubElementType
import com.intellij.psi.tree.IElementType
import org.c3lang.intellij.psi.C3TypeName
import org.c3lang.intellij.psi.FullyQualifiedName
import org.c3lang.intellij.psi.ModuleName
import org.c3lang.intellij.stubs.C3TypeEnum
import org.c3lang.intellij.stubs.C3TypeNameStub

abstract class C3TypeNameMixinImpl : C3StubBasedPsiElementBase<C3TypeNameStub>, C3TypeName {

    constructor(node: ASTNode) : super(node)

    constructor(
        stub: C3TypeNameStub,
        nodeType: IStubElementType<*, *>
    ) : super(stub, nodeType)

    constructor(
        stub: C3TypeNameStub,
        nodeType: IElementType?,
        node: ASTNode?
    ) : super(stub, nodeType, node)

    override val moduleName
        get() = stub?.moduleName ?: ModuleName.from(this)

    override val fqName
        get() = stub?.fqName ?: FullyQualifiedName.from(this)

    override val typeEnum: C3TypeEnum
        get() = stub?.typeEnum ?: C3TypeEnum.find(this)

}