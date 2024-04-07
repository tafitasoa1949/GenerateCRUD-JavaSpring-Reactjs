import React from 'react'

const Form = React.lazy(() => import('./Form'))
const Liste = React.lazy(() => import('./Liste'))
const Update = React.lazy(() => import('./Update'))

const routes = [
  { path: '/insert', name: 'Insertion', element: Form },
  { path: '/', name: 'Liste', element: Liste },
  { path: '/update/:id', name: 'Update', element: Update },
]

export default routes
