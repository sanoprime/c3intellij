package org.c3lang.intellij.psi.impl

import com.intellij.lang.ASTNode
import com.intellij.psi.PsiReferenceBase
import org.c3lang.intellij.psi.C3CallExpr
import org.c3lang.intellij.psi.C3ImportPath
import org.c3lang.intellij.psi.C3PathIdentExpr

abstract class C3CallExprMixinImpl(node: ASTNode) : C3PsiElementImpl(node), C3CallExpr {

//    override fun getNameIdentifier(): PsiElement? {
//        return node.findChildByType(C3Types.PATH_IDENT_EXPR)
//            ?.findChildByType(C3Types.PATH_IDENT)
//            ?.getChildren(TokenSet.create(C3Types.IDENT))
//            ?.lastOrNull()?.psi
//    }
//
//    override fun getName(): String? {
//        return nameIdentifier?.text
//    }
//
//    override fun setName(name: String): PsiElement {
//        return this
//    }

//    override fun getReference(): PsiReference = PsiMultiReference(
//        arrayOf(
//            ImportPathReference(this)
//        ),
//        this
//    )

}

