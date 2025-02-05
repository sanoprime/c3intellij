package org.c3lang.intellij.psi.impl

import com.intellij.lang.ASTNode
import com.intellij.psi.stubs.IStubElementType
import com.intellij.psi.tree.IElementType
import org.c3lang.intellij.psi.C3ConstDeclarationStmt
import org.c3lang.intellij.psi.FullyQualifiedName
import org.c3lang.intellij.psi.ModuleName
import org.c3lang.intellij.stubs.C3ConstDeclarationStmtStub

abstract class C3ConstDeclarationStmtMixinImpl :
    C3StubBasedPsiElementBase<C3ConstDeclarationStmtStub>, C3ConstDeclarationStmt {

    constructor(node: ASTNode) : super(node)

    constructor(
        stub: C3ConstDeclarationStmtStub,
        nodeType: IStubElementType<*, *>
    ) : super(stub, nodeType)

    constructor(
        stub: C3ConstDeclarationStmtStub,
        nodeType: IElementType?,
        node: ASTNode?
    ) : super(stub, nodeType, node)

    override val fqName: FullyQualifiedName
        get() = stub?.name ?: FullyQualifiedName.from(this, ModuleName.from(this))

}